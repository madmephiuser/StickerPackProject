/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import entity.StickerPackEntity;
import facade.StickerPackFacade;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StickerGenerationService {

    private final StickerPackFacade stickerPackFacade;
    public StickerGenerationService(@Lazy StickerPackFacade stickerPackFacade) {
        this.stickerPackFacade = stickerPackFacade;
    }

    @Async
    @Transactional
    public void generateAsync(StickerPackEntity pack, List<String> emotions) {
        stickerPackFacade.performGeneration(pack, emotions);
    }
}
