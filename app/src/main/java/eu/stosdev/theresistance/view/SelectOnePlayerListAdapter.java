package eu.stosdev.theresistance.view;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;

public class SelectOnePlayerListAdapter extends AbstractPlayerListAdapter {

    private List<Player> players = new ArrayList<>();

    @Inject
    public SelectOnePlayerListAdapter(Context context, GameState gameState) {
        super(context, gameState);
    }

    public void setPlayerList(List<Player> players) {
        this.players = players;
        notifyDataSetChanged();
    }

    @Override public int getCount() {
        return players.size();
    }

    @Override public Player getItem(int position) {
        return players.get(position);
    }
}
