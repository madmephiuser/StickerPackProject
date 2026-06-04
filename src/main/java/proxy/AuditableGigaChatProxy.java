/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proxy;

import entity.GigaChatAuditLog;
import repository.GigaChatAuditRepository;
import service.ImageGenerationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary 
public class AuditableGigaChatProxy implements ImageGenerationService {

    private final ImageGenerationService realService; 
    private final GigaChatAuditRepository auditRepository;

    public AuditableGigaChatProxy(@Qualifier("gigaChatImageService") ImageGenerationService realService, 
                                  GigaChatAuditRepository auditRepository) {
        this.realService = realService;
        this.auditRepository = auditRepository;
    }

    @Override
    public byte[] generateStickerImage(String emotion, String systemPrompt) {
        if (!checkAccess(emotion)) {
            throw new SecurityException("Доступ запрещен для генерации изображения: " + emotion);
        }
        
        GigaChatAuditLog log = new GigaChatAuditLog();
        log.setPrompt("User prompt: " + emotion + " | System prompt: " + systemPrompt);
        
        try {
            byte[] imageBytes = realService.generateStickerImage(emotion, systemPrompt);           
            log.setStatus("SUCCESS");
            log.setStatusCode(200);
            log.setResponseOrError("Изображение успешно загружено. Размер: " + imageBytes.length + " байт.");
            return imageBytes;
        } catch (Exception ex) {
            log.setStatus("FAILED");
            log.setStatusCode(500);
            log.setResponseOrError(ex.getMessage());
            throw ex;
        } finally {
            auditRepository.save(log);
        }
    }

    private boolean checkAccess(String emotion) {
        if (emotion.toLowerCase().contains("запрещенная")) {
            return false;
        }      
        return true; 
    }
}