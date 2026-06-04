/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

public class StickerPackVersionDTO {
    
    private Long id;
    private int version;
    private String visualStyle;
    private int ironyLevel;
    private String analysisMode;
    private int totalStickers;
    private String title;

    public StickerPackVersionDTO(Long id, int version, String title, String visualStyle, int ironyLevel, String analysisMode, int totalStickers) {
        this.id = id;
        this.version = version;
        this.title = title;
        this.visualStyle = visualStyle;
        this.ironyLevel = ironyLevel;
        this.analysisMode = analysisMode;
        this.totalStickers = totalStickers;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getVisualStyle() { return visualStyle; }
    public void setVisualStyle(String visualStyle) { this.visualStyle = visualStyle; }

    public int getIronyLevel() { return ironyLevel; }
    public void setIronyLevel(int ironyLevel) { this.ironyLevel = ironyLevel; }

    public String getAnalysisMode() { return analysisMode; }
    public void setAnalysisMode(String analysisMode) { this.analysisMode = analysisMode; }

    public int getTotalStickers() { return totalStickers; }
    public void setTotalStickers(int totalStickers) { this.totalStickers = totalStickers; }
}
