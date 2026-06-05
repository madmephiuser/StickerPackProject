/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dto.StickerPackDetailResponse;
import dto.StickerPackRequest;
import dto.StickerPackResponse;
import dto.StickerPackVersionDTO;
import entity.UserEntity;
import facade.StickerPackFacade;
import repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sticker-packs")
public class StickerPackController {

    private final StickerPackFacade stickerPackFacade;
    private final UserRepository userRepository;

    public StickerPackController(StickerPackFacade stickerPackFacade, UserRepository userRepository) {
        this.stickerPackFacade = stickerPackFacade;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<StickerPackResponse> createPack(
            @Valid @RequestBody StickerPackRequest request, 
            Principal principal) {

        UserEntity user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        StickerPackResponse response = stickerPackFacade.createStickerPack(request, user);
        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/{id}/regenerate")
    public ResponseEntity<StickerPackResponse> regeneratePack(
            @PathVariable Long id,
            @RequestBody StickerPackRequest request,
            Principal principal) {
        StickerPackResponse response = stickerPackFacade.regenerateStickerPack(id, request, getUserId(principal));
        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/{id}/stickers/regenerate")
    public ResponseEntity<StickerPackResponse> regenerateSticker(
            @PathVariable Long id,
            @RequestParam String emotion,
            Principal principal) {
        StickerPackResponse response = stickerPackFacade.regenerateSingleSticker(id, emotion, getUserId(principal));
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping
    public ResponseEntity<List<StickerPackResponse>> getHistory(Principal principal) {
        UserEntity user = userRepository.findByUsername(principal.getName()).orElseThrow();
        List<StickerPackResponse> history = stickerPackFacade.getUserStickerPacks(user.getId());
        return ResponseEntity.ok(history);
    }
    
    @PatchMapping("/{id}/save")
    public ResponseEntity<Void> savePack(@PathVariable Long id, Principal principal) {
        stickerPackFacade.saveStickerPack(id, getUserId(principal));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/compare")
    public ResponseEntity<List<StickerPackVersionDTO>> getCompareData(@PathVariable Long id, Principal principal) {
        List<StickerPackVersionDTO> versions = stickerPackFacade.getPackVersionsForComparison(id, getUserId(principal));
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<List<StickerPackResponse>> getPackVersions(@PathVariable Long id, Principal principal) {
        List<StickerPackResponse> versions = stickerPackFacade.getPackVersions(id, getUserId(principal));
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StickerPackDetailResponse> getPackDetails(@PathVariable Long id, Principal principal) {
        StickerPackDetailResponse pack = stickerPackFacade.getPackById(id, getUserId(principal));
        return ResponseEntity.ok(pack);
    }

    // достаём id текущего пользователя по его логину
    private Long getUserId(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"))
                .getId();
    }
}