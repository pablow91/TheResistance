package eu.stosdev.theresistance.model.messages;

import android.support.annotation.NonNull;

import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.model.messages.utils.AbsMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class RevealIdentityAcknowledgeMessage extends AbsMessage<RevealIdentityAcknowledgeMessage.Event> {

    private String cardOwner;
    private int cardPosition;
    private String from;
    private String to;

    @NonNull @Override public Event getEvent(String participantId) {
        return new Event(participantId);
    }

    public class Event extends AbsEvent {
        private Event(String participantId) {
            super(participantId);
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public String getCardOwner() {
            return cardOwner;
        }

        public int getCardPosition() {
            return cardPosition;
        }
    }
}