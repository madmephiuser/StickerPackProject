/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;

public interface ImageGenerationService {
    byte[] generateStickerImage(String emotion, String systemPrompt);
}
