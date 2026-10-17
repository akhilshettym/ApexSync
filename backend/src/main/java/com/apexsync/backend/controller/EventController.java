package com.apexsync.backend.controller;

import com.apexsync.backend.entity.Event;
import com.apexsync.backend.entity.Seat;
import com.apexsync.backend.repository.EventRepository;
import com.apexsync.backend.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    @GetMapping
    public ResponseEntity<List<Event>> getAllActiveEvents() {
        List<Event> events = eventRepository.findAll().stream()
                .filter(e -> e.getStatus() == Event.EventStatus.ACTIVE)
                .toList();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<Seat>> getEventSeats(@PathVariable Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        List<Seat> seats = seatRepository.findByVenueId(event.getVenueId());
        return ResponseEntity.ok(seats);
    }
}