package eu.stosdev.theresistance.view;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;

public class SelectPlayerForCardAdapter extends AbstractPlayerListAdapter {

    private List<Player> playerList = new ArrayList<>();

    @Inject
    public SelectPlayerForCardAdapter(Context context, GameState gameState) {
        super(context, gameState);
    }

    @Override public int getCount() {
        return playerList.size();
    }

    @Override public Player getItem(int position) {
        return playerList.get(position);
    }

    public void setPlayers(List<Player> playerList) {
        this.playerList = playerList;
        notifyDataSetChanged();
    }
}
