package eu.stosdev.theresistance.model.messages;

import android.support.annotation.NonNull;

import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.model.messages.utils.AbsMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InTheSpotlightMessage extends AbsMessage<InTheSpotlightMessage.Event> {

    private String playerId;

    public InTheSpotlightMessage(String playerId, boolean redistributable) {
        this.playerId = playerId;
        this.redistributable = redistributable;
    }

    @NonNull @Override public Event getEvent(String participantId) {
        return new Event(participantId);
    }

    public class Event extends AbsEvent {
        private Event(String participantId) {
            super(participantId);
        }

        public String getPlayerId() {
            return playerId;
        }

        public boolean isRedistributable() {
            return redistributable;
        }
    }
}