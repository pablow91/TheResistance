package eu.stosdev.theresistance.model.card;

import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.screen.TakeResponsibilityScreen;
import lombok.Getter;
import lombok.Setter;

public class TakeResponsibilityCard extends RevealIdentityCard {

    @Getter @Setter private Class<? extends PlotCard> plotCardClass;
    @Getter @Setter private boolean unused;

    @Override public int getName() {
        return R.string.take_responsibility;
    }

    @Override public int getDrawable() {
        return R.drawable.take_responsibility;
    }

    @Override public Object takeAction() {
        return new TakeResponsibilityScreen(this);
    }
}
