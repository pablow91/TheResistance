package eu.stosdev.theresistance.model.card;

import android.support.annotation.NonNull;

import eu.stosdev.theresistance.R;

public class OpinionMakerCard extends PlotCard {
    @NonNull @Override public Type getType() {
        return Type.PERMANENT;
    }

    @Override public int getName() {
        return R.string.opinion_maker;
    }

    @Override public int getDrawable() {
        return R.drawable.opinion_maker;
    }
}
