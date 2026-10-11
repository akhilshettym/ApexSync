package com.apexsync.backend.controller;

import com.apexsync.backend.dto.LockSeatRequest;
import com.apexsync.backend.entity.Reservation;
import com.apexsync.backend.service.SeatLockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final SeatLockService seatLockService;

    @PostMapping("/lock")
    public ResponseEntity<?> lockSeat(@Valid @RequestBody LockSeatRequest request) {
        try {
            Reservation reservation = seatLockService.lockSeat(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}