package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import com.google.common.base.Predicate;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.model.game.StrongLeaderVote;
import eu.stosdev.theresistance.model.messages.AcknowledgeVoteMessage;
import eu.stosdev.theresistance.model.messages.VoteFinishedMessage;
import eu.stosdev.theresistance.model.messages.VoteResponseMessage;
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.utils.MessageSender;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.FabController;
import eu.stosdev.theresistance.view.StrongLeaderView;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Layout(R.layout.strong_screen_view)
public class StrongLeaderScreen implements Blueprint {

    public StrongLeaderScreen() {
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = StrongLeaderView.class, addsTo = GameMainScreen.Module.class)
    public static class Module {
    }

    @Singleton
    public static class Presenter extends ViewPresenter<StrongLeaderView> {

        private final GameState gameState;
        private final String myParticipationId;
        private final StrongLeaderVote strongLeaderVote;
        private final Flow flow;
        private final FabController fabController;
        private final MessageSender messageSender;
        private final TypedBus<AbsEvent> bus;
        private final StartTurnDispatcher startTurnDispatcher;
        private final ToolbarController toolbarController;

        @Inject
        public Presenter(GameState gameState, @Named("myParticipantId") String myParticipationId, @Named("game") Flow flow, FabController fabController, MessageSender messageSender, TypedBus<AbsEvent> bus, StartTurnDispatcher startTurnDispatcher, ToolbarController toolbarController) {
            this.gameState = gameState;
            this.myParticipationId = myParticipationId;
            this.flow = flow;
            this.fabController = fabController;
            this.messageSender = messageSender;
            this.bus = bus;
            this.startTurnDispatcher = startTurnDispatcher;
            this.toolbarController = toolbarController;
            this.strongLeaderVote = gameState.getCurrentTurn().getStrongLeaderVote();
        }

        @Override protected void onEnterScope(MortarScope scope) {
            bus.register(this);
        }

        @Override protected void onExitScope() {
            bus.unregister(this);
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            toolbarController.setTitle(R.string.strong_leader);
            final List<String> voters = strongLeaderVote.getVoters();
            List<Player> filteredPlayerList = gameState.getFilteredPlayerList(new Predicate<Player>() {
                @Override public boolean apply(Player input) {
                    return voters.contains(input.getParticipantId());
                }
            });
            boolean haveStrongLeader = voters.contains(myParticipationId);
            toolbarController.setState(haveStrongLeader);
            getView().setPlayerList(filteredPlayerList);
            if (haveStrongLeader) {
                getView().activateButtons();
            } else {
                getView().activateList();
            }
            onVoteFinished();
        }

        public void onContinue() {
            messageSender.sendMessage(new VoteResponseMessage(true));
            getView().activateList();
        }

        public void onBecome() {
            messageSender.sendMessage(new VoteResponseMessage(false));
            getView().disableViews();
        }

        @Subscribe public void subscribeAcknowledgeVoteMessage(AcknowledgeVoteMessage.Event avm) {
            getView().notifyDataSetChanged();
        }

        @Subscribe public void subscribeVoteFinishedEvent(VoteFinishedMessage.Event vfe) {
            strongLeaderVote.parseFinishedMessage(vfe);
            onVoteFinished();
        }

        private void onVoteFinished() {
            if (strongLeaderVote.isCompleted()) {
                for (Map.Entry<String, Boolean> entry : strongLeaderVote.getAllVotes().entrySet()) {
                    if (!entry.getValue()) {
                        gameState.changeLeader(entry.getKey());
                        flow.goTo(new StrongNewLeaderScreen());
                        return;

                    }
                }
                fabController.show(new Runnable() {
                    @Override public void run() {
                        flow.goTo(startTurnDispatcher.goToNewTurn(true));
                    }
                });
            }
        }

    }
}

