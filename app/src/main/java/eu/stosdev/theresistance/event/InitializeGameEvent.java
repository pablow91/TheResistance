package eu.stosdev.theresistance.event;

import java.util.Collections;
import java.util.List;


public class InitializeGameEvent implements LocalEvent {
    private final List<String> participants;
    private final boolean usePlotCards;

    public InitializeGameEvent(List<String> participantStringList, boolean usePlotCards) {
        this.participants = participantStringList;
        this.usePlotCards = usePlotCards;
    }

    public List<String> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    public boolean getUsePlotCards() {
        return usePlotCards;
    }
}
