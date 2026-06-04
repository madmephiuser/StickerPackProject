package dto;

import java.util.List;

public class StickerPackDetailResponse {
    private Long id;
    private String title;
    private String status;
    private String errorMessage;
    private String visualStyle;
    private int ironyLevel;
    private String analysisMode;
    private int version;
    private boolean saved;
    private List<StickerResponse> stickers;

    public StickerPackDetailResponse(Long id, String title, String status, String errorMessage,
                                     String visualStyle, int ironyLevel, String analysisMode,
                                     int version, boolean saved, List<StickerResponse> stickers) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.errorMessage = errorMessage;
        this.visualStyle = visualStyle;
        this.ironyLevel = ironyLevel;
        this.analysisMode = analysisMode;
        this.version = version;
        this.saved = saved;
        this.stickers = stickers;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public String getVisualStyle() { return visualStyle; }
    public int getIronyLevel() { return ironyLevel; }
    public String getAnalysisMode() { return analysisMode; }
    public int getVersion() { return version; }
    public boolean isSaved() { return saved; }
    public List<StickerResponse> getStickers() { return stickers; }
}
