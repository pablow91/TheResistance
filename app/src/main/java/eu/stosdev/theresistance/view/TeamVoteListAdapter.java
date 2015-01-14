package eu.stosdev.theresistance.view;

import android.content.Context;

import javax.inject.Inject;

import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.model.game.TeamVote;

public class TeamVoteListAdapter extends AbstractPlayerListAdapter {

    private final TeamVote teamVote;

    @Inject
    public TeamVoteListAdapter(Context context, GameState gameState, TeamVote teamVote) {
        super(context, gameState);
        this.teamVote = teamVote;
    }

    @Override public int getCount() {
        return teamVote.getTeamMembers().size();
    }

    @Override public Player getItem(int position) {
        return gameState.getPlayerByParticipant(teamVote.getTeamMembers().get(position));
    }
}
