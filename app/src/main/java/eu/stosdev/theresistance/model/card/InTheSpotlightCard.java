package eu.stosdev.theresistance.model.card;

import android.support.annotation.NonNull;

import eu.stosdev.theresistance.R;

public class InTheSpotlightCard extends PlotCard {
    @NonNull @Override public Type getType() {
        return Type.ONE_TIME;
    }

    @Override public int getName() {
        return R.string.in_the_spotlight;
    }
    @Override public int getDrawable() {
        return R.drawable.in_the_spotlight;
    }
}
