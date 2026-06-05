/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import exception.RateLimitException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    // лимит и окно задаются в application.properties
    @Value("${generation.rate-limit.max-requests:10}")
    private int maxRequests;

    @Value("${generation.rate-limit.window-minutes:60}")
    private int windowMinutes;

    // для каждого пользователя храним время его последних обращений к генерации
    private final Map<Long, Deque<Instant>> userRequests = new ConcurrentHashMap<>();

    public synchronized void checkLimit(Long userId) {
        Instant now = Instant.now();
        Instant windowStart = now.minus(windowMinutes, ChronoUnit.MINUTES);

        Deque<Instant> requests = userRequests.computeIfAbsent(userId, k -> new ArrayDeque<>());

        while (!requests.isEmpty() && requests.peekFirst().isBefore(windowStart)) {
            requests.pollFirst();
        }

        if (requests.size() >= maxRequests) {
            throw new RateLimitException("Превышен лимит генераций: не больше " + maxRequests
                    + " за " + windowMinutes + " минут. Попробуйте позже.");
        }

        requests.addLast(now);
    }
}

