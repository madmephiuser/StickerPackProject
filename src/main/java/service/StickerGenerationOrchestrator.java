/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import entity.StickerEntity;
import entity.StickerPackEntity;
import repository.StickerPackRepository;
import strategy.PromptStrategyFactory;
import strategy.StickerPromptContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StickerGenerationOrchestrator {

    private final ImageGenerationService imageGenerationService;
    private final PromptStrategyFactory promptStrategyFactory;
    private final StickerPackRepository stickerPackRepository;

    public StickerGenerationOrchestrator(ImageGenerationService imageGenerationService,
                                       PromptStrategyFactory promptStrategyFactory,
                                       StickerPackRepository stickerPackRepository) {
        this.imageGenerationService = imageGenerationService;
        this.promptStrategyFactory = promptStrategyFactory;
        this.stickerPackRepository = stickerPackRepository;
    }

    @Transactional
    public void orchestrate(StickerPackEntity pack, List<String> emotions) {
        try {
            StickerPromptContext context = promptStrategyFactory.createContext(pack.getAnalysisMode());
            String systemPrompt = context.executeStrategy(pack.getVisualStyle(), pack.getIronyLevel());

            for (String emotion : emotions) {
                byte[] imageBytes = imageGenerationService.generateStickerImage(emotion, systemPrompt);
                
                StickerEntity sticker = new StickerEntity();
                sticker.setEmotion(emotion);
                sticker.setImageBytes(imageBytes);
                sticker.setStickerPack(pack);
                pack.getStickers().add(sticker);
            }
            pack.setStatus("SUCCESS");
        } catch (Exception e) {
            pack.setStatus("ERROR");
            pack.setErrorMessage(e.getMessage());
        }
        stickerPackRepository.save(pack);
    }

    // перегенерируем только один стикер, остальные копируем из прошлой версии без изменений
    @Transactional
    public void regenerateOne(StickerPackEntity pack, List<StickerEntity> oldStickers, String targetEmotion) {
        try {
            StickerPromptContext context = promptStrategyFactory.createContext(pack.getAnalysisMode());
            String systemPrompt = context.executeStrategy(pack.getVisualStyle(), pack.getIronyLevel());

            for (StickerEntity old : oldStickers) {
                StickerEntity sticker = new StickerEntity();
                sticker.setEmotion(old.getEmotion());
                sticker.setStickerPack(pack);

                if (old.getEmotion().equals(targetEmotion)) {
                    // выбранный стикер генерируем заново
                    sticker.setImageBytes(imageGenerationService.generateStickerImage(old.getEmotion(), systemPrompt));
                } else {
                    sticker.setImageBytes(old.getImageBytes());
                }
                pack.getStickers().add(sticker);
            }
            pack.setStatus("SUCCESS");
        } catch (Exception e) {
            pack.setStatus("ERROR");
            pack.setErrorMessage(e.getMessage());
        }
        stickerPackRepository.save(pack);
    }
}

