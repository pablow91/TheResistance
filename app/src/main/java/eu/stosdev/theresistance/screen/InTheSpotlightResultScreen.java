package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import com.squareup.otto.Subscribe;

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
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.FabController;
import eu.stosdev.theresistance.view.InTheSpotlightResultView;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Layout(R.layout.in_the_spotlight_result_view)
public class InTheSpotlightResultScreen implements Blueprint {

    private final MissionVote missionVote;

    public InTheSpotlightResultScreen(MissionVote missionVote) {
        this.missionVote = missionVote;
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = InTheSpotlightResultView.class, addsTo = GameMainScreen.Module.class)
    public class Module {
        @Provides @Singleton MissionVote provideMissionVote() {
            return missionVote;
        }
    }

    @Singleton
    public static class Presenter extends ViewPresenter<InTheSpotlightResultView> {

        private final GameState gameState;
        private final Flow flow;
        private final TypedBus<AbsEvent> bus;
        private final FabController fabController;
        private final MissionVote missionVote;
        private final ToolbarController toolbarController;

        @Inject
        public Presenter(GameState gameState, @Named("game") Flow flow, TypedBus<AbsEvent> bus, FabController fabController, MissionVote missionVote, ToolbarController toolbarController) {
            this.gameState = gameState;
            this.flow = flow;
            this.bus = bus;
            this.fabController = fabController;
            this.missionVote = missionVote;
            this.toolbarController = toolbarController;
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            toolbarController.setTitle(R.string.in_the_spotlight);
            playerLoop:
            for (Player player : gameState.getPlayerList()) {
                for (PlotCard plotCard : player.getCardHand()) {
                    if (plotCard instanceof InTheSpotlightCard) {
                        getView().setCardOwnerPlayer(player.getNick());
                        break playerLoop;
                    }
                }
            }
            if (missionVote.isBla()) {
                onFinished();
            } else {
                toolbarController.setState(false);
            }
        }

        @Override protected void onEnterScope(MortarScope scope) {
            bus.register(this);
        }

        @Override protected void onExitScope() {
            bus.unregister(this);
        }

        @Subscribe public void subscribeInTheSpotlightEvent(InTheSpotlightMessage.Event itse) {
            onFinished();
        }

        private void onFinished() {
            if (missionVote.getPublicResult() == null) {
                getView().setSpotlightedPlayer(null);
            } else {
                getView().setSpotlightedPlayer(gameState.getPlayerByParticipant(missionVote.getPublicResult()).getNick());
            }
            toolbarController.setState(true);
            fabController.show(new Runnable() {
                @Override public void run() {
                    flow.goTo(new MissionVoteScreen(missionVote));
                }
            });
        }
    }
}

