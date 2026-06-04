/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import org.springframework.stereotype.Service;

@Service
public class AccessControlService {
    
    public boolean canGenerateImage(String emotion, String username) {

        if (isForbiddenEmotion(emotion)) {
            return false;
        }
        return true;
    }
    
    private boolean isForbiddenEmotion(String emotion) {
        return emotion.equalsIgnoreCase("насилие") ||
               emotion.equalsIgnoreCase("агрессия");
    }
    
}
