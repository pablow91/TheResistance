package eu.stosdev.theresistance.model.messages;

import android.support.annotation.NonNull;

import java.util.Map;

import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.model.messages.utils.AbsMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class VoteFinishedMessage extends AbsMessage<VoteFinishedMessage.Event> {

    private Map<String, Boolean> votes;

    @NonNull @Override
    public Event getEvent(String participantId) {
        return new Event(participantId);
    }

    public class Event extends AbsEvent {
        private boolean used;

        private Event(String participantId) {
            super(participantId);
        }

        public Map<String, Boolean> getVotes() {
            used = true;
            return votes;
        }

        public boolean isUsed() {
            return used;
        }
    }
}
