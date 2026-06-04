/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package strategy;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class PromptStrategyFactory {
    private final Map<String, PromptStrategy> strategies;

    public PromptStrategyFactory(Map<String, PromptStrategy> strategies) {
        this.strategies = strategies;
    }

    public StickerPromptContext createContext(String mode) {
        PromptStrategy strategy = strategies.get(mode.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Неизвестный режим анализа: " + mode);
        }
        return new StickerPromptContext(strategy);
    }
}
