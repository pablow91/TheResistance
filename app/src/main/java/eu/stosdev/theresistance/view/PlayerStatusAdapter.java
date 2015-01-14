package eu.stosdev.theresistance.view;

import android.content.Context;

import javax.inject.Inject;

import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;

public class PlayerStatusAdapter extends AbstractPlayerListAdapter {

    @Inject
    public PlayerStatusAdapter(Context context, GameState gameState) {
        super(context, gameState);
    }

    @Override public int getCount() {
        return gameState.getPlayerList().size();
    }

    @Override public Player getItem(int position) {
        return gameState.getPlayerList().get(position);
    }
}
