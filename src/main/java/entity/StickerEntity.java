/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "stickers")
public class StickerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String emotion;
    
    @Lob
    private byte[] imageBytes;

    @ManyToOne
    @JoinColumn(name = "pack_id")
    private StickerPackEntity stickerPack;

    public StickerEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmotion() { return emotion; }
    public void setEmotion(String emotion) { this.emotion = emotion; }
    public byte[] getImageBytes() { return imageBytes; }
    public void setImageBytes(byte[] imageBytes) { this.imageBytes = imageBytes; }
    public StickerPackEntity getStickerPack() { return stickerPack; }
    public void setStickerPack(StickerPackEntity stickerPack) { this.stickerPack = stickerPack; }
}
