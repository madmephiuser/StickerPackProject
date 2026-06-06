/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.StickerPackRequest;
import entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import repository.UserRepository;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import stickerpackproject.StickerPackProject;

@SpringBootTest(classes = StickerPackProject.class)
@AutoConfigureMockMvc
@Transactional
class StickerPackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String authHeader;

    @BeforeEach
    void setUp() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setRole("USER");
        userRepository.save(user);
        
        String auth = "testuser:password";
        authHeader = "Basic " + java.util.Base64.getEncoder().encodeToString(auth.getBytes());
    }

    @Test
    void shouldCreateStickerPack() throws Exception {
        StickerPackRequest request = new StickerPackRequest();
        request.setTitle("Мой первый пак");
        request.setEmotions(List.of("Успех", "Страх"));
        request.setVisualStyle("Мем");
        request.setIronyLevel(3);
        request.setAnalysisMode("NEUTRAL");

        mockMvc.perform(post("/api/v1/sticker-packs")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.title").value("Мой первый пак"))
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    void shouldGetUserHistory() throws Exception {
        StickerPackRequest request = new StickerPackRequest();
        request.setTitle("Пак для истории");
        request.setEmotions(List.of("Радость"));
        request.setVisualStyle("Аниме");
        request.setIronyLevel(2);
        request.setAnalysisMode("SUPPORTIVE");

        mockMvc.perform(post("/api/v1/sticker-packs")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        mockMvc.perform(get("/api/v1/sticker-packs")
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Пак для истории"));
    }

    @Test
    void shouldDenyAccessWithoutAuth() throws Exception {
        StickerPackRequest request = new StickerPackRequest();
        request.setTitle("Неавторизованный");
        request.setEmotions(List.of("Тест"));
        request.setVisualStyle("Тест");
        request.setIronyLevel(1);
        request.setAnalysisMode("NEUTRAL");

        mockMvc.perform(post("/api/v1/sticker-packs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}

