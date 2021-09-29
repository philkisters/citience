package org.citience.communication;

import org.citience.communication.events.CommunicationEvent;

public interface CommunicationEventConsumer {

    void consume(CommunicationEvent event);
}
