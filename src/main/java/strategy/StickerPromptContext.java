/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package strategy;

public class StickerPromptContext {
    
    private final PromptStrategy strategy;

    public StickerPromptContext(PromptStrategy strategy) {
        this.strategy = strategy;
    }

    public String executeStrategy(String visualStyle, int ironyLevel) {
        return this.strategy.buildSystemPrompt(visualStyle, ironyLevel);
    }
}
