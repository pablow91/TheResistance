package eu.stosdev.theresistance.screen;

import javax.inject.Inject;
import javax.inject.Named;

import eu.stosdev.theresistance.model.card.PlotCard;
import eu.stosdev.theresistance.model.card.StrongLeaderCard;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.model.game.Turn;
import eu.stosdev.theresistance.view.ToolbarController;

public class StartTurnDispatcher {
    private final GameState gameState;
    private final ToolbarController toolbarController;
    private final String myParticipantId;

    @Inject
    public StartTurnDispatcher(GameState gameState, ToolbarController toolbarController, @Named("myParticipantId") String myParticipantId) {
        this.gameState = gameState;
        this.toolbarController = toolbarController;
        this.myParticipantId = myParticipantId;
    }

    public Object goToNewTurn() {
        return goToNewTurn(false);
    }

    public Object goToNewTurn(boolean afterStrongUser) {
        toolbarController.setCurrentLeader(gameState.getCurrentLeader());
        toolbarController.setFraction(gameState.getPlayerTypeMap().get(gameState.getPlayerByParticipant(myParticipantId)));
        Turn.State currentState = gameState.getCurrentTurn().getCurrentState();

        boolean haveStrongLeader = false;
        if (!afterStrongUser) {
            for (Player player : gameState.getPlayerList()) {
                if (player == gameState.getCurrentLeader()) {
                    continue;
                }
                for (PlotCard plotCard : player.getCardHand()) {
                    if (plotCard instanceof StrongLeaderCard) {
                        haveStrongLeader = true;
                        break;
                    }
                }
            }
        }
        if (gameState.isGameCompleted()) {
            return new WinnerScreen();
        } else if (haveStrongLeader) {
            return new StrongLeaderScreen();
        } else if (gameState.isExpansionCard() && currentState == Turn.State.CARD_PICKING) {
            return new CardPickingScreen();
        } else if (currentState == Turn.State.TEAM_PICKING || currentState == Turn.State.TEAM_VOTING) {
            return new ChooseTeamScreen();
        }
        throw new IllegalStateException("Not proper state " + currentState);
    }
}