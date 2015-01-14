package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.event.LocalEvent;
import eu.stosdev.theresistance.event.StartNewTurnEvent;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.MissionVote;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.FabController;
import eu.stosdev.theresistance.view.MissionResultView;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;

@Layout(R.layout.mission_result)
public class MissionResultScreen implements Blueprint {

    private final MissionVote missionVote;

    public MissionResultScreen(MissionVote missionVote) {
        this.missionVote = missionVote;
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = MissionResultView.class, addsTo = GameMainScreen.Module.class)
    public class Module {
        @Provides @Singleton MissionVote provideMissionVote() {
            return missionVote;
        }
    }

    @Singleton
    public static class Presenter extends ViewPresenter<MissionResultView> {

        private final Flow flow;
        private final MissionVote missionVote;
        private final ToolbarController toolbarController;
        private final FabController fabController;
        private final TypedBus<LocalEvent> eventBus;
        private final GameState gameState;
        private final StartTurnDispatcher startTurnDispatcher;

        @Inject
        public Presenter(@Named("game") Flow flow, MissionVote missionVote, ToolbarController toolbarController, FabController fabController, TypedBus<LocalEvent> eventBus, GameState gameState, StartTurnDispatcher startTurnDispatcher) {
            this.flow = flow;
            this.missionVote = missionVote;
            this.toolbarController = toolbarController;
            this.fabController = fabController;
            this.eventBus = eventBus;
            this.gameState = gameState;
            this.startTurnDispatcher = startTurnDispatcher;
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            toolbarController.setState(true);
            toolbarController.setTitle(R.string.mission_result);
            fabController.show(new Runnable() {
                @Override public void run() {
                    eventBus.post(new StartNewTurnEvent());
                    flow.goTo(startTurnDispatcher.goToNewTurn());
                }
            });
            int approved = 0, against = 0;
            for (Boolean entry : missionVote.getAllVotes().values()) {
                if (entry) {
                    approved++;
                } else {
                    against++;
                }
            }
            boolean verdict = missionVote.getVerdict();
            getView().setResults(approved, against, verdict);
            toolbarController.setMissionVoteResult(gameState.getCurrentTurnNum() - 1, verdict ? Player.Type.RESISTANCE : Player.Type.SPY);
            if (missionVote.getPublicResult() != null) {
                getView().setPublic(gameState.getPlayerByParticipant(missionVote.getPublicResult()).getNick(), missionVote.getAllVotes().get(missionVote.getPublicResult()));
            }
        }
    }
}

