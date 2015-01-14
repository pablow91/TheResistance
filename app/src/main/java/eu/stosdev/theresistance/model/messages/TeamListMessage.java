package eu.stosdev.theresistance.model.messages;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.model.messages.utils.AbsMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class TeamListMessage extends AbsMessage<TeamListMessage.Event> {

    private ArrayList<String> teamMembers;

    @NonNull @Override
    public Event getEvent(String participantId) {
        return new Event(participantId);
    }

    public class Event extends AbsEvent {
        private Event(String participantId) {
            super(participantId);
        }

        public ArrayList<String> getTeamMembers() {
            return teamMembers;
        }
    }
}

