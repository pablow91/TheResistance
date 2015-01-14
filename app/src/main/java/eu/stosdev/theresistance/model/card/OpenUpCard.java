package eu.stosdev.theresistance.model.card;

import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.screen.RevealMyIdentityScreen;

public class OpenUpCard extends RevealIdentityCard {

    @Override public int getName() {
        return R.string.open_up;
    }

    @Override public int getDrawable() {
        return R.drawable.open_up;
    }

    @Override public Object takeAction() {
        return new RevealMyIdentityScreen(this);
    }
}
