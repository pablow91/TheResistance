package eu.stosdev.theresistance.model.game;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import eu.stosdev.theresistance.model.card.NoConfidenceCard;
import eu.stosdev.theresistance.model.card.OpinionMakerCard;
import eu.stosdev.theresistance.model.card.PlotCard;
import eu.stosdev.theresistance.model.card.StrongLeaderCard;
import lombok.Getter;

import static com.google.common.base.Preconditions.checkState;
import static eu.stosdev.theresistance.model.game.Player.Type;

public class Turn {
    private final GameState gameState;

    @Getter private State currentState;
    private final List<TeamVote> teamVotes = new ArrayList<>();
    private MissionVote missionVote;

    private final List<PlotCard> pickedCards = new ArrayList<>();
    private int assigned;

    private StrongLeaderVote strongLeaderVote;

    @Inject
    public Turn(GameState gameState) {
        this.gameState = gameState;
        if (gameState.isExpansionCard()) {
            List<String> players = new ArrayList<>();
            for (Player player : gameState.getPlayerList()) {
                if (player == gameState.getCurrentLeader()) {
                    continue;
                }
                for (PlotCard plotCard : player.getCardHand()) {
                    if (plotCard instanceof StrongLeaderCard) {
                        players.add(player.getParticipantId());
                        break;
                    }
                }
            }
            if (!players.isEmpty()) {
                currentState = State.STRONG_LEADER;
                strongLeaderVote = new StrongLeaderVote(players, new Vote.OnVoteCompleted() {
                    @Override public void onVoteCompleted(boolean result) {
                        currentState = State.CARD_PICKING;
                    }
                });
            } else {
                currentState = State.CARD_PICKING;
            }

        } else {
            currentState = State.TEAM_PICKING;
        }
    }

    public boolean assignPlayerToCard(int position, String player) {
        PlotCard plotCard = pickedCards.get(position);
        if (plotCard.getOwner() != null) {
            return false;
        }
        plotCard.setOwner(player);
        gameState.getPlayerByParticipant(player).getCardHand().add(plotCard);
        assigned++;
        if (assigned == pickedCards.size()) {
            currentState = State.TEAM_PICKING;
        }
        return true;
    }

    public void pickTeam(List<String> team) {
        checkState(currentState == State.TEAM_PICKING, "Cannot start vote on current state: " + currentState);
        int noConfidenceCounter = 0;
        List<String> playersWithOpinionMaker = new ArrayList<>();
        if (gameState.isExpansionCard()) {
            for (Player player : gameState.getPlayerList()) {
                for (PlotCard plotCard : player.getCardHand()) {
                    if (plotCard instanceof NoConfidenceCard) {
                        noConfidenceCounter++;
                    } else if (plotCard instanceof OpinionMakerCard) {
                        playersWithOpinionMaker.add(player.getParticipantId());
                    }
                }
            }
        }
        List<String> playerAsString = gameState.getPlayerAsString();
        playerAsString.removeAll(playersWithOpinionMaker);
        teamVotes.add(new TeamVote(playerAsString, playersWithOpinionMaker, team, noConfidenceCounter, new Vote.OnVoteCompleted() {

            @Override
            public void onVoteCompleted(boolean result) {
                parseTeamVoteResults(result);
            }
        }));
        currentState = State.TEAM_VOTING;
    }

    public TeamVote getCurrentTeamVote() {
        checkState(currentState == State.TEAM_VOTING, "There is no team vote in progress");
        return teamVotes.get(teamVotes.size() - 1);
    }

    public MissionVote getMissionVote() {
        return missionVote;
    }

    public StrongLeaderVote getStrongLeaderVote() {
        checkState(currentState == State.STRONG_LEADER, "There is no strong vote in progress");
        return strongLeaderVote;
    }

    public Vote getCurrentVote() {
        switch (currentState) {
            case STRONG_LEADER:
                return strongLeaderVote;
            case TEAM_VOTING:
                return getCurrentTeamVote();
            case MISSION_VOTING:
                return missionVote;
            default:
                throw new IllegalStateException("There is no vote in progress");
        }
    }

    public boolean isVoteInProgress() {
        return currentState == State.TEAM_VOTING || currentState == State.MISSION_VOTING || currentState == State.STRONG_LEADER;
    }

    public Type getWinner() {
        return missionVote.getVerdict() ? Type.RESISTANCE : Type.SPY;
    }

    private void parseTeamVoteResults(boolean result) {
        if (result) {
            missionVote = new MissionVote(getCurrentTeamVote().getTeamMembers(), new Vote.OnVoteCompleted() {
                @Override public void onVoteCompleted(boolean result) {
                    currentState = State.FINISHED;
                    gameState.onTurnFinished();
                }
            });
            currentState = State.MISSION_VOTING;
        } else {
            gameState.changeLeader();
            currentState = State.TEAM_PICKING;
        }
    }

    public List<PlotCard> getPickedCards() {
        return pickedCards;
    }

    public int getIndexOfVote(TeamVote teamVote) {
        return teamVotes.indexOf(teamVote);
    }

    public boolean isSpyWin() {
        return teamVotes.size() == 5 && teamVotes.get(4).isApproved() && !teamVotes.get(4).getVerdict();
    }

    public enum State {
        STRONG_LEADER, CARD_PICKING, TEAM_PICKING, TEAM_VOTING, MISSION_VOTING, FINISHED
    }
}
