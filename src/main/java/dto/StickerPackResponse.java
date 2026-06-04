/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

public class StickerPackResponse {
    private Long id;
    private String title;
    private String status;
    private int totalStickers;
    private boolean saved;
    private int version;

    public StickerPackResponse(Long id, String title, String status, int totalStickers, boolean saved, int version) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.totalStickers = totalStickers;
        this.saved = saved;
        this.version = version;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public int getTotalStickers() { return totalStickers; }
    public boolean isSaved() { return saved; }
    public int getVersion() { return version; }
}
