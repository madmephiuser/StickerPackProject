/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;
import jakarta.validation.constraints.*;
import java.util.List;

public class StickerPackRequest {
    @NotBlank(message = "Название не может быть пустым")
    private String title;
    
    @NotEmpty(message = "Список эмоций не может быть пустым")
    private List<@NotBlank String> emotions;
    
    private String visualStyle;
    
    @Min(value = 0, message = "Ирония минимум 0") 
    @Max(value = 5, message = "Ирония максимум 5")
    private int ironyLevel;
    
    @Pattern(regexp = "SUPPORTIVE|STRICT|NEUTRAL", message = "Неверный режим анализа")
    private String analysisMode;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getEmotions() { return emotions; }
    public void setEmotions(List<String> emotions) { this.emotions = emotions; }
    public String getVisualStyle() { return visualStyle; }
    public void setVisualStyle(String visualStyle) { this.visualStyle = visualStyle; }
    public int getIronyLevel() { return ironyLevel; }
    public void setIronyLevel(int ironyLevel) { this.ironyLevel = ironyLevel; }
    public String getAnalysisMode() { return analysisMode; }
    public void setAnalysisMode(String analysisMode) { this.analysisMode = analysisMode; }
}

