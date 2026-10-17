package com.apexsync.backend.service;

import com.apexsync.backend.entity.Reservation;
import com.apexsync.backend.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final StringRedisTemplate redisTemplate;
    private final ReservationRepository reservationRepository;

    private static final String LOCK_KEY_PREFIX = "seat:lock:";

    @Transactional
    public Reservation confirmPayment(Long seatId, Long userId) {
        String lockKey = LOCK_KEY_PREFIX + seatId;
        String lockValue = "user:" + userId;

        String currentLockOwner = redisTemplate.opsForValue().get(lockKey);
        if (currentLockOwner == null || !currentLockOwner.equals(lockValue)) {
            throw new IllegalStateException("Seat lock has expired or is held by another user.");
        }

        Reservation reservation = reservationRepository.findAll().stream()
                .filter(r -> r.getSeat().getId().equals(seatId)
                        && r.getUser().getId().equals(userId)
                        && r.getStatus() == Reservation.ReservationStatus.HELD)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No active reservation found for this seat."));

        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        Reservation savedReservation = reservationRepository.save(reservation);

        redisTemplate.delete(lockKey);
        redisTemplate.opsForValue().set("seat:sold:" + seatId, "user:" + userId);

        return savedReservation;
    }
}