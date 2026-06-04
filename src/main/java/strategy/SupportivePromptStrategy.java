/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package strategy;

import org.springframework.stereotype.Component;

@Component("SUPPORTIVE")
public class SupportivePromptStrategy implements PromptStrategy {
    @Override
    public String buildSystemPrompt(String visualStyle, int ironyLevel) {
    return String.format(
            "Ты — поддерживающий художник-иллюстратор. Твоя задача — подбодрить студента на сессии. " +
            "Создай добрый мем-стикер. Визуальный стиль: %s. Уровень мягкой иронии: %d/5.", 
            visualStyle, ironyLevel
        );
    }
}
