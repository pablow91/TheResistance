package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.card.PlotCard;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.model.messages.AssignPlotCardMessage;
import eu.stosdev.theresistance.utils.MessageSender;
import eu.stosdev.theresistance.view.SelectPlayerForCardView;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;

@Layout(R.layout.select_player_for_card)
public class SelectPlayerForCardScreen implements Blueprint {

    private final int position;
    private final PlotCard plotCard;

    public SelectPlayerForCardScreen(int position, PlotCard plotCard) {
        this.position = position;
        this.plotCard = plotCard;
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = SelectPlayerForCardView.class, addsTo = GameMainScreen.Module.class)
    public class Module {
        @Provides @Singleton int providePosition() {
            return position;
        }

        @Provides @Singleton PlotCard providePlotCard() {
            return plotCard;
        }
    }

    @Singleton
    public static class Presenter extends ViewPresenter<SelectPlayerForCardView> {

        private final GameState gameState;
        private final int position;
        private final MessageSender messageSender;
        private final ToolbarController toolbarController;
        private final Flow flow;
        private final PlotCard plotCard;

        @Inject
        public Presenter(GameState gameState, int position, MessageSender messageSender, ToolbarController toolbarController, @Named("game") Flow flow, PlotCard plotCard) {
            this.gameState = gameState;
            this.position = position;
            this.messageSender = messageSender;
            this.toolbarController = toolbarController;
            this.flow = flow;
            this.plotCard = plotCard;
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            toolbarController.setTitle(gameState.getCurrentTurn().getPickedCards().get(position).getName());
            toolbarController.setState(true);
            List<Player> playerListWithoutLeader = new ArrayList<>();
            for (Player player : gameState.getPlayerList()) {
                if (player != gameState.getCurrentLeader()) {
                    playerListWithoutLeader.add(player);
                }
            }
            getView().setPlayers(playerListWithoutLeader);
        }

        public void onPlayerSelected(Player player) {
            flow.goTo(new CardPickingScreen());
            messageSender.sendMessage(new AssignPlotCardMessage(position, player.getParticipantId()));
        }
    }
}

