package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Turn;
import eu.stosdev.theresistance.model.messages.TeamVoteMessage;
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.ChooseTeamAwaitingView;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Layout(R.layout.main_game_screen)
public class ChooseTeamAwaitingScreen implements Blueprint {

    public ChooseTeamAwaitingScreen() {
    }

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = ChooseTeamAwaitingView.class, addsTo = GameMainScreen.Module.class)
    public static class Module {
    }

    @Singleton
    public static class Presenter extends ViewPresenter<ChooseTeamAwaitingView> {

        private final Flow flow;
        private final GameState gameState;
        private final ToolbarController toolbarController;
        private final TypedBus<AbsEvent> bus;

        @Inject
        public Presenter(@Named("game") Flow flow, GameState gameState, ToolbarController toolbarController, TypedBus<AbsEvent> bus) {
            this.flow = flow;
            this.gameState = gameState;
            this.toolbarController = toolbarController;
            this.bus = bus;
        }

        @Override protected void onEnterScope(MortarScope scope) {
            bus.register(this);
        }

        @Override protected void onExitScope() {
            bus.unregister(this);
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            if (gameState.getCurrentTurn().getCurrentState() != Turn.State.TEAM_PICKING) {
                moveToVoteScreen();
                return;
            }
            toolbarController.setState(false);
            toolbarController.setTitle(R.string.team_picking);
            getView().setPlayerList(gameState.getPlayerList());
        }

        @Subscribe public void subscribeTeamVoteEvent(TeamVoteMessage.Event teamVoteEvent) {
            moveToVoteScreen();
        }

        private void moveToVoteScreen() {
            flow.goTo(new TeamVoteScreen(gameState.getCurrentTurn().getCurrentTeamVote()));
        }
    }
}
