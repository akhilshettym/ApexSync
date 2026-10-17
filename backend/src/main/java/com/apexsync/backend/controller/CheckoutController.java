package com.apexsync.backend.controller;

import com.apexsync.backend.dto.LockSeatRequest;
import com.apexsync.backend.entity.Reservation;
import com.apexsync.backend.service.CheckoutService;
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
    private final CheckoutService checkoutService;

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

    @PostMapping("/pay")
    public ResponseEntity<?> confirmPayment(@RequestParam Long seatId, @RequestParam Long userId) {
        try {
            Reservation reservation = checkoutService.confirmPayment(seatId, userId);
            return ResponseEntity.ok(reservation);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}