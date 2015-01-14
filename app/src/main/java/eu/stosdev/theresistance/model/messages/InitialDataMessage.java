package eu.stosdev.theresistance.model.messages;

import android.support.annotation.NonNull;

import java.util.List;

import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.model.messages.utils.AbsMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class InitialDataMessage extends AbsMessage<InitialDataMessage.Event> {

    private List<String> playerList;
    private String currentLeader;
    private Player.Type myType;
    private List<String> spies;
    private boolean usePlotCards;

    @NonNull @Override
    public Event getEvent(String participantId) {
        return new Event(participantId);
    }

    public class Event extends AbsEvent {
        private Event(String participantId) {
            super(participantId);
        }

        public List<String> getPlayerList() {
            return playerList;
        }

        public String getCurrentLeader() {
            return currentLeader;
        }

        public Player.Type getMyType() {
            return myType;
        }

        public List<String> getSpies() {
            return spies;
        }

        public boolean getUsePlotCards() {
            return usePlotCards;
        }
    }
}
