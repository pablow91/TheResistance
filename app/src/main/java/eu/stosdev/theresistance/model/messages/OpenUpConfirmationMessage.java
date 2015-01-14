package eu.stosdev.theresistance.model.messages;

import android.support.annotation.NonNull;

import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.model.messages.utils.AbsMessage;

public class OpenUpConfirmationMessage extends AbsMessage<OpenUpConfirmationMessage.Event> {

    private final String cardOwner;
    private final int cardPosition;
    private final String from;
    private final String to;

    public OpenUpConfirmationMessage(String cardOwner, int cardPosition, String from, String to) {
        this.cardOwner = cardOwner;
        this.cardPosition = cardPosition;
        this.from = from;
        this.to = to;
    }

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