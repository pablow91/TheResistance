package eu.stosdev.theresistance.model.card;

import android.support.annotation.NonNull;

public abstract class InstantPlotCard extends PlotCard {
    @NonNull @Override public final Type getType() {
        return Type.INSTANT;
    }

    public abstract Object takeAction();
}
