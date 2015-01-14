package eu.stosdev.theresistance.model.messages.utils;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public abstract class AbsEvent {
    private final String participantId;

    protected AbsEvent(String participantId) {
        this.participantId = participantId;
    }

    public String getParticipantId() {
        return participantId;
    }
}
