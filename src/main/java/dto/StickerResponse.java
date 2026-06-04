/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

public class StickerResponse {
    private Long id;
    private String emotion;
    private byte[] imageBytes;

    public StickerResponse(Long id, String emotion, byte[] imageBytes) {
        this.id = id;
        this.emotion = emotion;
        this.imageBytes = imageBytes;
    }

    public Long getId() { return id; }
    public String getEmotion() { return emotion; }
    public byte[] getImageBytes() { return imageBytes; }
}

