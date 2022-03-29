package org.citience.communication;

import org.citience.communication.events.CommunicationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommunicationService {
    private static final Logger log = LoggerFactory.getLogger(CommunicationService.class);

    Map<String, List<CommunicationEventConsumer>> consumers;

    public CommunicationService() {
        consumers = new HashMap<>();
    }

    @Async
    public void publishCommunicationEvent(CommunicationEvent event) {
        if (consumers.containsKey(event.getEventName())) {
            consumers.get(event.getEventName()).parallelStream().forEach(consumer -> consumer.consume(event));
            log.debug("All subscribers have consumed the event '{}'.", event);
        } else {
            log.debug("No event consumers registered for the event '{}', dropping it.", event.getEventName());
        }
    }

    public void subscribeToCommunicationEvent(final String eventName, CommunicationEventConsumer consumer) {
        consumers.computeIfAbsent(eventName, eventList -> new ArrayList<>()).add(consumer);
    }

    Map<String, List<CommunicationEventConsumer>> getConsumers() {
        return consumers;
    }
}
