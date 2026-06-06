/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package facade;

import dto.StickerPackDetailResponse;
import dto.StickerPackRequest;
import dto.StickerPackResponse;
import entity.StickerPackEntity;
import entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import repository.StickerPackRepository;
import service.RateLimitService;
import service.StickerGenerationOrchestrator;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StickerPackFacadeTest {

    @Mock StickerPackRepository stickerPackRepository;
    @Mock StickerGenerationOrchestrator orchestrator;
    @Mock RateLimitService rateLimitService;

    @InjectMocks StickerPackFacade facade;

    // пак пользователя с заданным id
    private StickerPackEntity packOfUser(Long ownerId) {
        UserEntity owner = new UserEntity();
        owner.setId(ownerId);
        owner.setUsername("masha");
        owner.setPasswordHash("секретный_хеш");

        StickerPackEntity pack = new StickerPackEntity();
        pack.setId(10L);
        pack.setTitle("Сессия");
        pack.setStatus("SUCCESS");
        pack.setVisualStyle("Мем");
        pack.setIronyLevel(3);
        pack.setAnalysisMode("NEUTRAL");
        pack.setUser(owner);
        return pack;
    }

    @Test
    void getPackByIdReturnsDtoForOwner() {
        when(stickerPackRepository.findById(10L)).thenReturn(Optional.of(packOfUser(1L)));

        StickerPackDetailResponse dto = facade.getPackById(10L, 1L);

        assertEquals("Сессия", dto.getTitle());
        assertEquals("NEUTRAL", dto.getAnalysisMode());
        assertEquals(3, dto.getIronyLevel());
    }

    @Test
    void getPackByIdDeniesForeignPack() {
        when(stickerPackRepository.findById(10L)).thenReturn(Optional.of(packOfUser(1L)));

        // пак принадлежит пользователю 1, а запрашивает пользователь 2
        assertThrows(AccessDeniedException.class, () -> facade.getPackById(10L, 2L));
    }

    @Test
    void getPackByIdThrowsWhenNotFound() {
        when(stickerPackRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> facade.getPackById(99L, 1L));
    }

    @Test
    void createStickerPackChecksLimitAndStartsAsProcessing() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        StickerPackRequest request = new StickerPackRequest();
        request.setTitle("Сессия");
        request.setEmotions(List.of("Успех"));
        request.setVisualStyle("Мем");
        request.setIronyLevel(3);
        request.setAnalysisMode("NEUTRAL");

        when(stickerPackRepository.save(any(StickerPackEntity.class)))
                .thenAnswer(inv -> { StickerPackEntity p = inv.getArgument(0); p.setId(5L); return p; });

        StickerPackResponse response = facade.createStickerPack(request, user);

        verify(rateLimitService).checkLimit(1L);
        verify(orchestrator).orchestrate(any(StickerPackEntity.class), eq(request.getEmotions()));
        assertEquals("PROCESSING", response.getStatus());
    }

    @Test
    void getUserStickerPacksMapsToResponses() {
        when(stickerPackRepository.findByUserId(1L)).thenReturn(List.of(packOfUser(1L)));

        List<StickerPackResponse> result = facade.getUserStickerPacks(1L);

        assertEquals(1, result.size());
        assertEquals("Сессия", result.get(0).getTitle());
    }
}

