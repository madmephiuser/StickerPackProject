/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package strategy;

import org.springframework.stereotype.Component;

@Component("NEUTRAL")
public class NeutralPromptStrategy implements PromptStrategy {
    @Override
    public String buildSystemPrompt(String visualStyle, int ironyLevel) {
        return String.format(
            "Ты — нейтральный художник-иллюстратор. Спокойно и без оценки изобрази ситуацию " +
            "студента на сессии, просто и понятно. Визуальный стиль: %s. Уровень иронии: %d/5.",
            visualStyle, ironyLevel
        );
    }
}
