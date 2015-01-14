package eu.stosdev.theresistance.model.messages.utils;

import android.support.annotation.NonNull;

import java.io.Serializable;

public abstract class AbsMessage<T extends AbsEvent> implements Serializable {
    protected boolean redistributable;

    @NonNull public abstract T getEvent(String participantId);
}
