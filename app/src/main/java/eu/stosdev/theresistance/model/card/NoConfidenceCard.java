package eu.stosdev.theresistance.model.card;

import android.support.annotation.NonNull;

import eu.stosdev.theresistance.R;

public class NoConfidenceCard extends PlotCard {
    @NonNull @Override public Type getType() {
        return Type.ONE_TIME;
    }

    @Override public int getName() {
        return R.string.no_confidence;
    }

    @Override public int getDrawable() {
        return R.drawable.no_confidence;
    }
}
