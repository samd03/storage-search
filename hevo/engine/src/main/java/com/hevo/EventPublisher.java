package com.hevo;

import com.hevo.listeners.EventListener;
import com.hevo.listeners.FileDetectionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.springframework.stereotype.Component;


@Component
public class EventPublisher {

    private Map<Event, Collection<EventListener>> subscriberMap = new HashMap<>();

    public void publish(Event event, Object payload) {
        subscriberMap.getOrDefault(event, Collections.emptyList()).forEach(s -> s.listen(payload));
    }

    public void subscribe(EventListener listener, Event event) {
        subscriberMap.computeIfAbsent(event, e -> new HashSet<>()).add(listener);
    }


}
