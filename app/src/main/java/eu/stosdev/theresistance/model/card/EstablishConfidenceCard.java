package eu.stosdev.theresistance.model.card;


import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.screen.RevealMyIdentityScreen;

public class EstablishConfidenceCard extends RevealIdentityCard {

    @Override public int getName() {
        return R.string.establish_confidence_card;
    }

    @Override public int getDrawable() {
        return R.drawable.establish_confidence;
    }

    @Override public Object takeAction() {
        return new RevealMyIdentityScreen(this);
    }
}
