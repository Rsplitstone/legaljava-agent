package com.legaljava;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventService {
    
    private final Map<String, Map<String, Object>> eventStorage = new ConcurrentHashMap<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public EventService() {
        // Initialize with some sample events
        initializeSampleEvents();
    }
    
    private void initializeSampleEvents() {
        Map<String, Object> event1 = new HashMap<>();
        event1.put("id", "1");
        event1.put("title", "Case Review Meeting");
        event1.put("start", "2025-05-30T09:00:00");
        event1.put("end", "2025-05-30T10:00:00");
        event1.put("type", "meeting");
        event1.put("priority", "high");
        event1.put("createdAt", LocalDateTime.now().format(formatter));
        
        Map<String, Object> event2 = new HashMap<>();
        event2.put("id", "2");
        event2.put("title", "Document Deadline");
        event2.put("start", "2025-06-01T17:00:00");
        event2.put("end", "2025-06-01T18:00:00");
        event2.put("type", "deadline");
        event2.put("priority", "medium");
        event2.put("createdAt", LocalDateTime.now().format(formatter));
        
        Map<String, Object> event3 = new HashMap<>();
        event3.put("id", "3");
        event3.put("title", "Client Consultation");
        event3.put("start", "2025-06-02T14:00:00");
        event3.put("end", "2025-06-02T15:30:00");
        event3.put("type", "consultation");
        event3.put("priority", "high");
        event3.put("createdAt", LocalDateTime.now().format(formatter));
        
        eventStorage.put("1", event1);
        eventStorage.put("2", event2);
        eventStorage.put("3", event3);
    }
    
    public List<Map<String, Object>> getAllEvents() {
        return new ArrayList<>(eventStorage.values());
    }
    
    public Map<String, Object> getEventById(String id) {
        return eventStorage.get(id);
    }
    
    public Map<String, Object> createEvent(Map<String, Object> eventData) {
        String id = UUID.randomUUID().toString();
        eventData.put("id", id);
        eventData.put("createdAt", LocalDateTime.now().format(formatter));
        
        // Validate required fields
        if (!eventData.containsKey("title") || !eventData.containsKey("start")) {
            throw new IllegalArgumentException("Event must have title and start time");
        }
        
        // Set default values if not provided
        if (!eventData.containsKey("type")) {
            eventData.put("type", "event");
        }
        if (!eventData.containsKey("priority")) {
            eventData.put("priority", "medium");
        }
        
        eventStorage.put(id, eventData);
        return eventData;
    }
    
    public Map<String, Object> updateEvent(String id, Map<String, Object> eventData) {
        Map<String, Object> existingEvent = eventStorage.get(id);
        if (existingEvent == null) {
            return null;
        }
        
        // Update fields that are provided
        existingEvent.putAll(eventData);
        existingEvent.put("id", id); // Ensure ID doesn't change
        existingEvent.put("updatedAt", LocalDateTime.now().format(formatter));
        
        eventStorage.put(id, existingEvent);
        return existingEvent;
    }
    
    public boolean deleteEvent(String id) {
        return eventStorage.remove(id) != null;
    }
    
    public List<Map<String, Object>> getEventsByDateRange(String startDate, String endDate) {
        // For now, return all events. In a real implementation,
        // this would filter events by the specified date range
        return getAllEvents();
    }
    
    public List<Map<String, Object>> getEventsByType(String type) {
        return eventStorage.values().stream()
                .filter(event -> type.equals(event.get("type")))
                .collect(ArrayList::new, (list, event) -> list.add(event), ArrayList::addAll);
    }
}
