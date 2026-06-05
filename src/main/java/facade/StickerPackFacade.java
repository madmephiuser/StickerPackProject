/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package facade;

import dto.StickerPackDetailResponse;
import dto.StickerPackRequest;
import dto.StickerPackResponse;
import dto.StickerPackVersionDTO;
import dto.StickerResponse;
import entity.StickerEntity;
import entity.StickerPackEntity;
import entity.UserEntity;
import repository.StickerPackRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Async;
import service.StickerGenerationOrchestrator;


@Service
public class StickerPackFacade {

    private final StickerPackRepository stickerPackRepository;
    private final StickerGenerationOrchestrator orchestrator;

    public StickerPackFacade(StickerPackRepository stickerPackRepository,
                             StickerGenerationOrchestrator orchestrator) {
        this.stickerPackRepository = stickerPackRepository;
        this.orchestrator = orchestrator;
    }

    // проверяем, что пак существует и принадлежит именно этому пользователю
    private StickerPackEntity getOwnedPack(Long id, Long userId) {
        StickerPackEntity pack = stickerPackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Стикер-пак с ID " + id + " не найден."));
        if (!pack.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Это не ваш стикер-пак.");
        }
        return pack;
    }

    // корневой пак
    private StickerPackEntity getRoot(StickerPackEntity pack) {
        return pack.getParentPack() == null ? pack : pack.getParentPack();
    }

    // все версии: корень + все его версии, отсортированные по номеру
    private List<StickerPackEntity> getFamily(StickerPackEntity pack) {
        StickerPackEntity root = getRoot(pack);
        List<StickerPackEntity> family = new ArrayList<>();
        family.add(root);
        family.addAll(stickerPackRepository.findByParentPackId(root.getId()));
        family.sort(Comparator.comparingInt(StickerPackEntity::getVersion));
        return family;
    }

    private int nextVersion(StickerPackEntity root) {
        return stickerPackRepository.findByParentPackId(root.getId()).stream()
                .mapToInt(StickerPackEntity::getVersion)
                .max()
                .orElse(root.getVersion()) + 1;
    }

    // собираем DTO ответа, чтобы не отдавать наружу сущность с пользователем и его паролем
    private StickerPackDetailResponse toDetailResponse(StickerPackEntity pack) {
        List<StickerResponse> stickers = pack.getStickers().stream()
                .map(s -> new StickerResponse(s.getId(), s.getEmotion(), s.getImageBytes()))
                .collect(Collectors.toList());

        return new StickerPackDetailResponse(
                pack.getId(),
                pack.getTitle(),
                pack.getStatus(),
                pack.getErrorMessage(),
                pack.getVisualStyle(),
                pack.getIronyLevel(),
                pack.getAnalysisMode(),
                pack.getVersion(),
                pack.isSaved(),
                stickers
        );
    }

    @Transactional
    public StickerPackResponse createStickerPack(StickerPackRequest request, UserEntity user) {

        StickerPackEntity pack = new StickerPackEntity();
        pack.setTitle(request.getTitle());
        pack.setStatus("PROCESSING");
        pack.setVisualStyle(request.getVisualStyle());
        pack.setIronyLevel(request.getIronyLevel());
        pack.setAnalysisMode(request.getAnalysisMode());
        pack.setUser(user);

        StickerPackEntity savedPack = stickerPackRepository.save(pack);

        performGeneration(savedPack, request.getEmotions());

        return new StickerPackResponse(savedPack.getId(), savedPack.getTitle(), "PROCESSING", 0, savedPack.isSaved(), savedPack.getVersion());
    }

    @Transactional
    public StickerPackResponse regenerateStickerPack(Long parentPackId, StickerPackRequest newParams, Long userId) {

        StickerPackEntity oldPack = getOwnedPack(parentPackId, userId);

        StickerPackEntity newVersionPack = new StickerPackEntity();
        newVersionPack.setTitle(newParams.getTitle() != null ? newParams.getTitle() : oldPack.getTitle());
        newVersionPack.setStatus("PROCESSING");
        newVersionPack.setVisualStyle(newParams.getVisualStyle() != null ? newParams.getVisualStyle() : oldPack.getVisualStyle());
        newVersionPack.setIronyLevel(newParams.getIronyLevel() > 0 ? newParams.getIronyLevel() : oldPack.getIronyLevel());
        newVersionPack.setAnalysisMode(newParams.getAnalysisMode() != null ? newParams.getAnalysisMode() : oldPack.getAnalysisMode());


        StickerPackEntity root = getRoot(oldPack);
        newVersionPack.setParentPack(root);
        newVersionPack.setVersion(nextVersion(root));
        newVersionPack.setUser(oldPack.getUser());

        StickerPackEntity savedPack = stickerPackRepository.save(newVersionPack);
        List<String> emotionsToGenerate = (newParams.getEmotions() != null && !newParams.getEmotions().isEmpty()) 
                ? newParams.getEmotions() : oldPack.getStickers().stream().map(StickerEntity::getEmotion).collect(Collectors.toList());

        performGeneration(savedPack, emotionsToGenerate);

        return new StickerPackResponse(savedPack.getId(), savedPack.getTitle(), savedPack.getStatus(), savedPack.getStickers().size(), savedPack.isSaved(), savedPack.getVersion());
    }

    @Transactional
    public StickerPackResponse regenerateSingleSticker(Long packId, String emotion, Long userId) {

        StickerPackEntity oldPack = getOwnedPack(packId, userId);

        // новая версия с теми же настройками — меняем только один стикер
        StickerPackEntity newVersionPack = new StickerPackEntity();
        newVersionPack.setTitle(oldPack.getTitle());
        newVersionPack.setStatus("PROCESSING");
        newVersionPack.setVisualStyle(oldPack.getVisualStyle());
        newVersionPack.setIronyLevel(oldPack.getIronyLevel());
        newVersionPack.setAnalysisMode(oldPack.getAnalysisMode());

        StickerPackEntity root = getRoot(oldPack);
        newVersionPack.setParentPack(root);
        newVersionPack.setVersion(nextVersion(root));
        newVersionPack.setUser(oldPack.getUser());

        StickerPackEntity savedPack = stickerPackRepository.save(newVersionPack);

        // выбранный стикер генерируется заново, остальные копируются из прошлой версии
        orchestrator.regenerateOne(savedPack, oldPack.getStickers(), emotion);

        return new StickerPackResponse(savedPack.getId(), savedPack.getTitle(), savedPack.getStatus(),
                savedPack.getStickers().size(), savedPack.isSaved(), savedPack.getVersion());
    }
    
    @Async
    public void performGeneration(StickerPackEntity pack, List<String> emotions) {
        orchestrator.orchestrate(pack, emotions);
    }

    @Transactional(readOnly = true)
    public List<StickerPackResponse> getUserStickerPacks(Long userId) {
        return stickerPackRepository.findByUserId(userId).stream()
                .map(pack -> new StickerPackResponse(
                        pack.getId(),
                        pack.getTitle(),
                        pack.getStatus(),
                        pack.getStickers().size(),
                        pack.isSaved(),
                        pack.getVersion()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveStickerPack(Long id, Long userId) {
        StickerPackEntity pack = getOwnedPack(id, userId);
        // переключаем: если был сохранён — снимаем, если нет — сохраняем
        pack.setSaved(!pack.isSaved());
        stickerPackRepository.save(pack);
    }

    @Transactional(readOnly = true)
    public List<StickerPackResponse> getPackVersions(Long packId, Long userId) {
        StickerPackEntity pack = getOwnedPack(packId, userId);
        // вся семья версий, кроме текущей
        return getFamily(pack).stream()
                .filter(p -> !p.getId().equals(packId))
                .map(p -> new StickerPackResponse(
                        p.getId(), p.getTitle(), p.getStatus(), p.getStickers().size(), p.isSaved(), p.getVersion()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StickerPackVersionDTO> getPackVersionsForComparison(Long packId, Long userId) {
        StickerPackEntity pack = getOwnedPack(packId, userId);
        // для сравнения берём всю семью версий целиком, включая текущую
        return getFamily(pack).stream()
                .map(p -> new StickerPackVersionDTO(
                        p.getId(), p.getVersion(), p.getTitle(),
                        p.getVisualStyle(), p.getIronyLevel(),
                        p.getAnalysisMode(), p.getStickers().size()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StickerPackDetailResponse getPackById(Long id, Long userId) {
        StickerPackEntity pack = getOwnedPack(id, userId);
        return toDetailResponse(pack);
    }
}