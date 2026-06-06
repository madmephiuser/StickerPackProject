/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import stickerpackproject.StickerPackProject;

@SpringBootTest(classes = StickerPackProject.class)
@AutoConfigureMockMvc
@Transactional  // после теста всё откатывается
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterNewUser() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .param("username", "student")
                .param("password", "12345"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void shouldNotRegisterDuplicateUser() throws Exception {
        // первый раз регистрируем
        mockMvc.perform(post("/api/v1/auth/register")
                .param("username", "student")
                .param("password", "12345"));

        // второй раз тот же пользователь - ошибка
        mockMvc.perform(post("/api/v1/auth/register")
                .param("username", "student")
                .param("password", "12345"))
                .andExpect(status().isConflict());
    }
}
