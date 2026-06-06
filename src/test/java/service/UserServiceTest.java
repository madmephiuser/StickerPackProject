/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks UserService userService;

    @Test
    void registersNewUserWithHashedPassword() {
        when(userRepository.findByUsername("masha")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("hashed");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.registerUser("masha", "1234");

        // проверяем, что сохранили пользователя с хешем
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity saved = captor.getValue();
        assertEquals("masha", saved.getUsername());
        assertEquals("hashed", saved.getPasswordHash());
        assertEquals("USER", saved.getRole());
    }

    @Test
    void rejectsAlreadyExistingUser() {
        when(userRepository.findByUsername("masha")).thenReturn(Optional.of(new UserEntity()));

        assertThrows(RuntimeException.class, () -> userService.registerUser("masha", "1234"));
        verify(userRepository, never()).save(any());
    }
}
