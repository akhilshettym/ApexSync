package com.apexsync.backend.service;

import com.apexsync.backend.dto.CreateEventRequest;
import com.apexsync.backend.entity.Event;
import com.apexsync.backend.entity.Seat;
import com.apexsync.backend.repository.EventRepository;
import com.apexsync.backend.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public Event createEventWithSeats(CreateEventRequest request) {
        // 1. Create and save the Event
        Event event = Event.builder()
                .title(request.title())
                .venueId(request.venueId())
                .basePrice(request.basePrice())
                .startTime(request.startTime())
                .status(Event.EventStatus.ACTIVE)
                .build();

        Event savedEvent = eventRepository.save(event);

        // 2. Generate seat rows dynamically based on section configs
        List<Seat> seatsToSave = new ArrayList<>();
        for (CreateEventRequest.SectionConfig section : request.sections()) {
            for (int r = 1; r <= section.rowCount(); r++) {
                String rowLetter = getRowIdentifier(r); // e.g., 1 -> A, 2 -> B
                for (int s = 1; s <= section.seatsPerRow(); s++) {
                    Seat seat = Seat.builder()
                            .venueId(request.venueId())
                            .section(section.sectionName())
                            .rowNum(rowLetter)
                            .seatNum(String.valueOf(s))
                            .build();
                    seatsToSave.add(seat);
                }
            }
        }

        // 3. Bulk save seats for maximum database throughput
        seatRepository.saveAll(seatsToSave);

        return savedEvent;
    }

    // Helper to convert row number to alphabetic identifier (A, B, C...)
    private String getRowIdentifier(int rowNumber) {
        StringBuilder result = new StringBuilder();
        while (rowNumber > 0) {
            rowNumber--;
            result.insert(0, (char) ('A' + (rowNumber % 26)));
            rowNumber /= 26;
        }
        return result.toString();
    }
}