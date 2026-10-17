package com.apexsync.backend.controller;

import com.apexsync.backend.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StringRedisTemplate redisTemplate;
    private final EventRepository eventRepository;

    @GetMapping("/system/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Set<String> activeLocks = redisTemplate.keys("seat:lock:*");
        long lockCount = activeLocks != null ? activeLocks.size() : 0;
        long totalEvents = eventRepository.count();

        return ResponseEntity.ok(Map.of(
                "status", "HEALTHY",
                "activeRedisLocks", lockCount,
                "totalRegisteredEvents", totalEvents,
                "jvmMemoryUsedMB", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)
        ));
    }
}