/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock UserRepository userRepository;

    @InjectMocks CustomUserDetailsService service;

    @Test
    void loadsExistingUser() {
        UserEntity user = new UserEntity();
        user.setUsername("masha");
        user.setPasswordHash("hashed");
        user.setRole("USER");
        when(userRepository.findByUsername("masha")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("masha");

        assertEquals("masha", details.getUsername());
        assertEquals("hashed", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void throwsWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("ghost"));
    }
}

