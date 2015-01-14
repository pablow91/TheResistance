package eu.stosdev.theresistance.model.messages;

import android.support.annotation.NonNull;

import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.model.messages.utils.AbsMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TakeResponsibilityMessage extends AbsMessage<TakeResponsibilityMessage.Event> {

    private String from;
    private String to;
    private int cardId;
    private int takeCardId;

    public TakeResponsibilityMessage(String from, String to, int cardId, int takeCardId, boolean redistributable) {
        this.from = from;
        this.to = to;
        this.cardId = cardId;
        this.takeCardId = takeCardId;
        this.redistributable = redistributable;
    }

    @NonNull @Override public Event getEvent(String participantId) {
        return new Event(participantId);
    }

    public class Event extends AbsEvent {
        private Event(String participantId) {
            super(participantId);
        }

        public boolean isRedistributable() {
            return redistributable;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public int getCardId() {
            return cardId;
        }

        public int getTakeCardId() {
            return takeCardId;
        }
    }
}