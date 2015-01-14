package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import com.google.android.gms.games.multiplayer.Participant;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.event.InitializeGameEvent;
import eu.stosdev.theresistance.event.LocalEvent;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.FabController;
import eu.stosdev.theresistance.view.ParticipantListView;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;

@Layout(R.layout.participant_list_view)
public class ParticipantListScreen implements Blueprint {

    private final List<Participant> participantList;

    public ParticipantListScreen(List<Participant> participantList) {
        this.participantList = participantList;
    }

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = ParticipantListView.class, addsTo = GameMainScreen.Module.class)
    public class Module {
        @Provides
        public List<Participant> provideParticipantList() {
            return participantList;
        }
    }

    @Singleton
    public static class Presenter extends ViewPresenter<ParticipantListView> {

        private final List<Participant> participantList;
        private final GameState gameState;
        private final FabController fabController;
        private final StartTurnDispatcher startTurnDispatcher;
        private final TypedBus<LocalEvent> eventBus;
        private final Flow flow;
        private final ToolbarController toolbarController;

        @Inject
        public Presenter(List<Participant> participantList, GameState gameState, FabController fabController, StartTurnDispatcher startTurnDispatcher, TypedBus<LocalEvent> eventBus, @Named("game") Flow flow, ToolbarController toolbarController) {
            this.participantList = participantList;
            this.gameState = gameState;
            this.fabController = fabController;
            this.startTurnDispatcher = startTurnDispatcher;
            this.eventBus = eventBus;
            this.flow = flow;
            this.toolbarController = toolbarController;
        }

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            toolbarController.setTitle(R.string.choose_order);
            toolbarController.show();
            fabController.show(new Runnable() {
                @Override public void run() {
                    onStartGameClicked();
                }
            });
        }

        public void onStartGameClicked() {
            gameState.generatePlayers(participantList, getView().usePlotCard());
            List<String> strings = new ArrayList<>(participantList.size());
            for (Participant participant : participantList) {
                strings.add(participant.getParticipantId());
            }
            eventBus.post(new InitializeGameEvent(strings, getView().usePlotCard()));
            flow.goTo(startTurnDispatcher.goToNewTurn());
        }
    }
}
