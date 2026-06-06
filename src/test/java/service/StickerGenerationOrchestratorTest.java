/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import entity.StickerPackEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.StickerPackRepository;
import strategy.NeutralPromptStrategy;
import strategy.PromptStrategyFactory;
import strategy.StickerPromptContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StickerGenerationOrchestratorTest {

    @Mock ImageGenerationService imageGenerationService;
    @Mock PromptStrategyFactory promptStrategyFactory;
    @Mock StickerPackRepository stickerPackRepository;

    @InjectMocks StickerGenerationOrchestrator orchestrator;

    private StickerPackEntity newPack() {
        StickerPackEntity pack = new StickerPackEntity();
        pack.setAnalysisMode("NEUTRAL");
        pack.setVisualStyle("Мем");
        pack.setIronyLevel(3);
        return pack;
    }

    @Test
    void setsSuccessAndCreatesStickers() {
        StickerPackEntity pack = newPack();
        when(promptStrategyFactory.createContext("NEUTRAL"))
                .thenReturn(new StickerPromptContext(new NeutralPromptStrategy()));
        when(imageGenerationService.generateStickerImage(anyString(), anyString()))
                .thenReturn(new byte[]{1, 2});

        orchestrator.orchestrate(pack, List.of("Успех", "Провал"));

        assertEquals("SUCCESS", pack.getStatus());
        assertEquals(2, pack.getStickers().size());
        verify(stickerPackRepository).save(pack);
    }

    @Test
    void setsErrorWhenGenerationFails() {
        StickerPackEntity pack = newPack();
        when(promptStrategyFactory.createContext("NEUTRAL"))
                .thenReturn(new StickerPromptContext(new NeutralPromptStrategy()));
        when(imageGenerationService.generateStickerImage(anyString(), anyString()))
                .thenThrow(new RuntimeException("GigaChat недоступен"));

        orchestrator.orchestrate(pack, List.of("Успех"));

        // при сбое API пак не теряется,а он сохраняется со статусом ERROR
        assertEquals("ERROR", pack.getStatus());
        assertNotNull(pack.getErrorMessage());
        verify(stickerPackRepository).save(pack);
    }
}

