/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package impl;

import service.ImageGenerationService;
import chat.giga.client.GigaChatClient;
import chat.giga.model.completion.ChatFunctionCallEnum;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessageRole;
import chat.giga.model.completion.CompletionRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Service("gigaChatImageService") 
public class GigaChatImageServiceImpl implements ImageGenerationService {

    private final GigaChatClient gigaChatClient;

    public GigaChatImageServiceImpl(GigaChatClient gigaChatClient) {
        this.gigaChatClient = gigaChatClient;
    }
    @Override
    @Retryable(
        retryFor = { RuntimeException.class },
        maxAttempts = 3, 
        backoff = @Backoff(delay = 2000)
    )
    public byte[] generateStickerImage(String emotion, String systemPrompt) {
        ChatMessage systemMessage = ChatMessage.builder()
                .role(ChatMessageRole.SYSTEM)
                .content(systemPrompt)
                .build();

        ChatMessage userMessage = ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .content("Сгенерируй изображение для эмоции: " + emotion)
                .build();

        CompletionRequest request = CompletionRequest.builder()
                .model("GigaChat")
                .messages(List.of(systemMessage, userMessage))
                .functionCall(ChatFunctionCallEnum.AUTO) //без этого модель не рисует
                .build();

        try {
            String responseText = gigaChatClient.completions(request)
                    .choices()
                    .get(0)
                    .message()
                    .content();
                    
            String fileId = extractFileId(responseText);
            if (fileId == null) {
                throw new RuntimeException("Модель не вернула изображение. Ответ сети: " + responseText);
            }

            return gigaChatClient.downloadFile(fileId, null);
            
        } catch (Exception e) {
            throw new RuntimeException("Ошибка интеграции с GigaChat API: " + e.getMessage(), e);
        }
    }

    private String extractFileId(String responseText) {
        Pattern pattern = Pattern.compile("src=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(responseText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
