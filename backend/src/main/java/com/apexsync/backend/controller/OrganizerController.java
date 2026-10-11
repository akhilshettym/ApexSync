package com.apexsync.backend.controller;

import com.apexsync.backend.dto.CreateEventRequest;
import com.apexsync.backend.entity.Event;
import com.apexsync.backend.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizer")
@RequiredArgsConstructor
public class OrganizerController {

    private final EventService eventService;

    @PostMapping("/events")
    public ResponseEntity<Event> createEvent(@Valid @RequestBody CreateEventRequest request) {
        Event createdEvent = eventService.createEventWithSeats(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }
}