package com.apexsync.backend.service;

import com.apexsync.backend.dto.LockSeatRequest;
import com.apexsync.backend.entity.Event;
import com.apexsync.backend.entity.Reservation;
import com.apexsync.backend.entity.Seat;
import com.apexsync.backend.entity.User;
import com.apexsync.backend.repository.EventRepository;
import com.apexsync.backend.repository.ReservationRepository;
import com.apexsync.backend.repository.SeatRepository;
import com.apexsync.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SeatLockService {

    private final StringRedisTemplate redisTemplate;
    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    private static final String LOCK_KEY_PREFIX = "seat:lock:";

    @Transactional
    public Reservation lockSeat(LockSeatRequest request) {
        String lockKey = LOCK_KEY_PREFIX + request.seatId();
        String lockValue = "user:" + request.userId();

        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, Duration.ofMinutes(10));

        if (Boolean.FALSE.equals(acquired)) {
            throw new IllegalStateException("Seat is already locked or sold by another user.");
        }

        Seat seat = seatRepository.findById(request.seatId())
                .orElseThrow(() -> new IllegalArgumentException("Seat not found"));
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Reservation reservation = Reservation.builder()
                .user(user)
                .event(event)
                .seat(seat)
                .status(Reservation.ReservationStatus.HELD)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        return reservationRepository.save(reservation);
    }
}