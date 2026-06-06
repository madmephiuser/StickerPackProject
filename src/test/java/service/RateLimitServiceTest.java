/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import exception.RateLimitException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

class RateLimitServiceTest {

    private RateLimitService service;

    @BeforeEach
    void setUp() {
        service = new RateLimitService();
        ReflectionTestUtils.setField(service, "maxRequests", 3);
        ReflectionTestUtils.setField(service, "windowMinutes", 60);
    }

    @Test
    void allowsRequestsWithinLimit() {
        Long userId = 1L;
        assertDoesNotThrow(() -> {
            service.checkLimit(userId);
            service.checkLimit(userId);
            service.checkLimit(userId);
        });
    }

    @Test
    void throwsWhenLimitExceeded() {
        Long userId = 1L;
        service.checkLimit(userId);
        service.checkLimit(userId);
        service.checkLimit(userId);
        // четвёртое обращение должно упереться в лимит
        assertThrows(RateLimitException.class, () -> service.checkLimit(userId));
    }

    @Test
    void limitIsCountedPerUser() {
        service.checkLimit(1L);
        service.checkLimit(1L);
        service.checkLimit(1L);
        // у другого пользователя другой счётчик
        assertDoesNotThrow(() -> service.checkLimit(2L));
    }
}
