package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.view.FabController;
import eu.stosdev.theresistance.view.StrongNewLeaderView;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;

@Layout(R.layout.strong_new_leader_view)
public class StrongNewLeaderScreen implements Blueprint {

    public StrongNewLeaderScreen() {
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = StrongNewLeaderView.class, addsTo = GameMainScreen.Module.class)
    public static class Module {
    }

    @Singleton
    public static class Presenter extends ViewPresenter<StrongNewLeaderView> {

        private final GameState gameState;
        private final ToolbarController toolbarController;
        private final FabController fabController;
        private final Flow flow;
        private final StartTurnDispatcher startTurnDispatcher;

        @Inject
        public Presenter(GameState gameState, ToolbarController toolbarController, FabController fabController, @Named("game") Flow flow, StartTurnDispatcher startTurnDispatcher) {
            this.gameState = gameState;
            this.toolbarController = toolbarController;
            this.fabController = fabController;
            this.flow = flow;
            this.startTurnDispatcher = startTurnDispatcher;
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            toolbarController.setTitle(R.string.new_leader);
            toolbarController.setState(true);
            toolbarController.setCurrentLeader(gameState.getCurrentLeader());
            getView().setNewLeader(gameState.getCurrentLeader().getNick());
            fabController.show(new Runnable() {
                @Override public void run() {
                    flow.goTo(startTurnDispatcher.goToNewTurn(true));
                }
            });
        }
    }
}

