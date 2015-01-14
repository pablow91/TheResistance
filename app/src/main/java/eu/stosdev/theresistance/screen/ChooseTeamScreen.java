package eu.stosdev.theresistance.screen;

import android.os.Bundle;
import android.util.SparseBooleanArray;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.model.game.Turn;
import eu.stosdev.theresistance.model.messages.TeamListMessage;
import eu.stosdev.theresistance.model.messages.TeamVoteMessage;
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.utils.MessageSender;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.ChooseTeamListItem;
import eu.stosdev.theresistance.view.ChooseTeamView;
import eu.stosdev.theresistance.view.FabController;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.MortarScope;
import mortar.ViewPresenter;

import static com.google.common.base.Preconditions.checkArgument;

@Layout(R.layout.choose_screen)
public class ChooseTeamScreen implements Blueprint {

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = ChooseTeamView.class, addsTo = GameMainScreen.Module.class, library = true)
    public static class Module {
        @Provides @Singleton
        ChooseTeamListItem.OnCheckCallback provideOnCheckCallback(Presenter presenter) {
            return presenter;
        }
    }

    @Singleton
    public static class Presenter extends ViewPresenter<ChooseTeamView> implements ChooseTeamListItem.OnCheckCallback {
        private final GameState gameState;
        private final MessageSender messageSender;
        private final FabController fabController;
        private final Flow flow;
        private final TypedBus<AbsEvent> bus;
        private final String myParticipantId;
        private final ToolbarController toolbarController;

        private final SparseBooleanArray results = new SparseBooleanArray();

        @Inject
        public Presenter(GameState gameState, MessageSender messageSender, FabController fabController, @Named("game") Flow flow, TypedBus<AbsEvent> bus, @Named("myParticipantId") String myParticipantId, ToolbarController toolbarController) {
            this.gameState = gameState;
            this.messageSender = messageSender;
            this.fabController = fabController;
            this.flow = flow;
            this.bus = bus;
            this.myParticipantId = myParticipantId;
            this.toolbarController = toolbarController;
        }

        @Override protected void onEnterScope(MortarScope scope) {
            bus.register(this);
        }

        @Override protected void onExitScope() {
            bus.unregister(this);
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            if (!gameState.getCurrentLeader().getParticipantId().equals(myParticipantId)) {
                flow.goTo(new ChooseTeamAwaitingScreen());
            } else if (gameState.getCurrentTurn().getCurrentState() != Turn.State.TEAM_PICKING) {
                flow.goTo(new TeamVoteScreen(gameState.getCurrentTurn().getCurrentTeamVote()));
            } else {
                toolbarController.setState(true);
                toolbarController.setTitle(R.string.team_picking);
            }
        }

        @Override public void onChange(int position, boolean status) {
            results.put(position, status);
            int sum = 0;
            for (int i = 0; i < results.size(); i++) {
                if (results.valueAt(i)) {
                    sum++;
                }
            }
            if (sum == teamSize(gameState)) {
                final List<Player> selected = new ArrayList<>();
                for (int i = 0; i < results.size(); i++) {
                    if (results.valueAt(i)) {
                        selected.add(gameState.getPlayerList().get(i));
                    }
                }
                fabController.show(new Runnable() {
                    @Override public void run() {
                        onPlayerSelected(selected);
                    }
                });
            } else {
                fabController.hide();
            }
        }

        private void onPlayerSelected(List<Player> selectedPlayers) {
            checkArgument(selectedPlayers.size() == teamSize(gameState));
            ArrayList<String> players = new ArrayList<>();
            for (Player selectedPlayer : selectedPlayers) {
                players.add(selectedPlayer.getParticipantId());
            }
            messageSender.sendMessage(new TeamListMessage(players));
        }

        @Subscribe public void subscribeTeamVoteEvent(TeamVoteMessage.Event teamVoteEvent) {
            flow.goTo(new TeamVoteScreen(gameState.getCurrentTurn().getCurrentTeamVote()));
        }

        private final int[][] teamSize = {{2, 2, 2, 3, 3, 3}, {3, 3, 3, 4, 4, 4}, {2, 4, 3, 4, 4, 4}, {3, 3, 4, 5, 5, 5}, {3, 4, 4, 5, 5, 5}};

        private int teamSize(GameState gameState) {
            return teamSize[gameState.getCurrentTurnNum() - 1][gameState.getPlayerList().size() - 5];
        }
    }
}
