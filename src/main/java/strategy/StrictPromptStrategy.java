/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package strategy;

import org.springframework.stereotype.Component;

@Component("STRICT")
public class StrictPromptStrategy implements PromptStrategy {
    @Override
    public String buildSystemPrompt(String visualStyle, int ironyLevel) {
        return String.format(
            "Ты — жесткий и прагматичный карикатурист. Отрази суровую реальность дедлайнов, " +
            "бессонных ночей и страшных профессоров. Визуальный стиль: %s. Уровень цинизма: %d/5.", 
            visualStyle, ironyLevel
        );
    }
}
