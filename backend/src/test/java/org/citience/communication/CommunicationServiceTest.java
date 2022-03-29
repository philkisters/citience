package org.citience.communication;

import org.citience.communication.events.CommunicationEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommunicationServiceTest {

    static CommunicationService communicationService;

    @BeforeEach
    void init () {
        communicationService = new CommunicationService();
    }

    @Nested
    class PublishCommunicationEvent {
        static DemoConsumer mockedConsumer;
        @BeforeEach
        void init () {
            List<CommunicationEventConsumer> consumers = new ArrayList<>();
            mockedConsumer = mock(DemoConsumer.class);
            consumers.add(mockedConsumer);

            communicationService.getConsumers().put("DemoEvent", consumers);
        }

        @Test
        void publishCommunicationEventWithExistingSubscribers () {
            DemoEvent demoEvent = new DemoEvent();

            communicationService.publishCommunicationEvent(demoEvent);

            verify(mockedConsumer, times(1)).consume(demoEvent);
        }

        @Test
        void publishCommunicationEventWithNoExistingSubscribers () {
            communicationService.publishCommunicationEvent(new AnotherDemoEvent());

            verify(mockedConsumer, never()).consume(any());

        }

    }



    @Test
    void subscribeToCommunicationEvent() {
        Assertions.assertEquals(0, communicationService.getConsumers().size());

        communicationService.subscribeToCommunicationEvent(DemoEvent.EVENT_NAME, new DemoConsumer());

        Assertions.assertEquals(1, communicationService.getConsumers().size());
        Assertions.assertTrue(communicationService.getConsumers().containsKey("DemoEvent"));
        Assertions.assertEquals(1, communicationService.getConsumers().get("DemoEvent").size());

        communicationService.subscribeToCommunicationEvent(DemoEvent.EVENT_NAME, new DemoConsumer());

        Assertions.assertEquals(1, communicationService.getConsumers().size());
        Assertions.assertEquals(2, communicationService.getConsumers().get("DemoEvent").size());

    }

    class DemoConsumer implements CommunicationEventConsumer {
        @Override
        public void consume(CommunicationEvent event) {
            // Do something
        }
    }

    class DemoEvent implements CommunicationEvent {
        public static final String EVENT_NAME = "demo";
        @Override
        public String getEventName() {
            return EVENT_NAME;
        }
    }

    class AnotherDemoEvent implements CommunicationEvent {
        @Override
        public String getEventName() {
            return "AnotherDemoEvent";
        }
    }
}