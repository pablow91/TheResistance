package eu.stosdev.theresistance.model.card;

import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.screen.OverheardConversationScreen;

public class OverheardConversationCard extends RevealIdentityCard {

    @Override public int getName() {
        return R.string.overheard_conversation;
    }

    @Override public int getDrawable() {
        return R.drawable.overheard_conversation;
    }

    @Override public Object takeAction() {
        return new OverheardConversationScreen(this);
    }
}
