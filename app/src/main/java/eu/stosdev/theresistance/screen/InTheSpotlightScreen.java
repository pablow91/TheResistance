package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import com.google.common.base.Predicate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.card.InTheSpotlightCard;
import eu.stosdev.theresistance.model.card.PlotCard;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.MissionVote;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.model.messages.InTheSpotlightMessage;
import eu.stosdev.theresistance.utils.MessageSender;
import eu.stosdev.theresistance.view.FabController;
import eu.stosdev.theresistance.view.InTheSpotlightView;
import eu.stosdev.theresistance.view.OnPlayerSelected;
import eu.stosdev.theresistance.view.SelectOnePlayerListView;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;

@Layout(R.layout.in_the_spotlight_view)
public class InTheSpotlightScreen implements Blueprint {

    private final MissionVote missionVote;

    public InTheSpotlightScreen(MissionVote missionVote) {
        this.missionVote = missionVote;
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = {InTheSpotlightView.class, Presenter.class}, addsTo = GameMainScreen.Module.class)
    public class Module {
        @Provides @Singleton MissionVote provideMissionVote() {
            return missionVote;
        }
    }

    @Singleton
    public static class Presenter extends ViewPresenter<SelectOnePlayerListView<Presenter>> implements OnPlayerSelected, Predicate<Player> {

        private final GameState gameState;
        private final Flow flow;
        private final String myParticipantId;
        private final MessageSender messageSender;
        private final FabController fabController;
        private final MissionVote missionVote;
        private final ToolbarController toolbarController;

        @Inject
        public Presenter(GameState gameState, @Named("game") Flow flow, @Named("myParticipantId") String myParticipantId, MessageSender messageSender, FabController fabController, MissionVote missionVote, ToolbarController toolbarController) {
            this.gameState = gameState;
            this.flow = flow;
            this.myParticipantId = myParticipantId;
            this.messageSender = messageSender;
            this.fabController = fabController;
            this.missionVote = missionVote;
            this.toolbarController = toolbarController;
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            for (PlotCard plotCard : gameState.getPlayerByParticipant(myParticipantId).getCardHand()) {
                if (plotCard instanceof InTheSpotlightCard) {
                    toolbarController.setState(true);
                    toolbarController.setTitle(R.string.in_the_spotlight);
                    getView().setPlayerList(gameState.getFilteredPlayerList(this));
                    fabController.show(new Runnable() {
                        @Override public void run() {
                            messageSender.sendMessage(new InTheSpotlightMessage(null, true));
                            fabController.hide();
                            flow.goTo(new InTheSpotlightResultScreen(missionVote));
                        }
                    });
                    return;
                }
            }
            flow.goTo(new InTheSpotlightResultScreen(missionVote));
        }

        @Override public void onPlayerSelected(Player player) {
            messageSender.sendMessage(new InTheSpotlightMessage(player.getParticipantId(), true));
            flow.goTo(new InTheSpotlightResultScreen(missionVote));
        }

        @Override public boolean apply(Player input) {
            return missionVote.getVoters().contains(input.getParticipantId()) && !input.getParticipantId().equals(myParticipantId);
        }
    }
}