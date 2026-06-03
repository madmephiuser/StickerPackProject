/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sticker_packs")
public class StickerPackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String status;
    
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "is_saved")
    private boolean isSaved = false;

    private String visualStyle;
    private int ironyLevel;
    private String analysisMode;

    private int version = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_pack_id")
    private StickerPackEntity parentPack;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "stickerPack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StickerEntity> stickers = new ArrayList<>();

    public StickerPackEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { isSaved = saved; }
    public String getVisualStyle() { return visualStyle; }
    public void setVisualStyle(String visualStyle) { this.visualStyle = visualStyle; }
    public int getIronyLevel() { return ironyLevel; }
    public void setIronyLevel(int ironyLevel) { this.ironyLevel = ironyLevel; }
    public String getAnalysisMode() { return analysisMode; }
    public void setAnalysisMode(String analysisMode) { this.analysisMode = analysisMode; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public StickerPackEntity getParentPack() { return parentPack; }
    public void setParentPack(StickerPackEntity parentPack) { this.parentPack = parentPack; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
    public List<StickerEntity> getStickers() { return stickers; }
    public void setStickers(List<StickerEntity> stickers) { this.stickers = stickers; }
}
