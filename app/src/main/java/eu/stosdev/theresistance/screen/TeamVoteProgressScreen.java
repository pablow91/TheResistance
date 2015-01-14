package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.card.InTheSpotlightCard;
import eu.stosdev.theresistance.model.card.NoConfidenceCard;
import eu.stosdev.theresistance.model.card.PlotCard;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.MissionVote;
import eu.stosdev.theresistance.model.game.TeamVote;
import eu.stosdev.theresistance.model.messages.AcknowledgeVoteMessage;
import eu.stosdev.theresistance.model.messages.NoConfidenceMessage;
import eu.stosdev.theresistance.model.messages.TeamVoteApprovalUpdateMessage;
import eu.stosdev.theresistance.model.messages.VoteFinishedMessage;
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.utils.MessageSender;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.FabController;
import eu.stosdev.theresistance.view.TeamVoteProgressView;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Layout(R.layout.team_vote_progress)
public class TeamVoteProgressScreen implements Blueprint {

    private final TeamVote currentVote;

    public TeamVoteProgressScreen(TeamVote currentVote) {
        this.currentVote = currentVote;
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = TeamVoteProgressView.class, addsTo = GameMainScreen.Module.class)
    public class Module {
        @Provides @Singleton TeamVote providesTeamVote() {
            return currentVote;
        }
    }

    @Singleton
    public static class Presenter extends ViewPresenter<TeamVoteProgressView> {

        private final TeamVote teamVote;
        private final TypedBus<AbsEvent> bus;
        private final Flow flow;
        private final ToolbarController toolbarController;
        private final FabController fabController;
        private final MessageSender messageSender;
        private final GameState gameState;
        private final String myParticipantId;
        private final int currentTurnId;

        private boolean voted;

        @Inject
        public Presenter(TeamVote teamVote, TypedBus<AbsEvent> bus, @Named("game") Flow flow, ToolbarController toolbarController, FabController fabController, MessageSender messageSender, GameState gameState, @Named("myParticipantId") String myParticipantId) {
            this.teamVote = teamVote;
            this.bus = bus;
            this.flow = flow;
            this.toolbarController = toolbarController;
            this.fabController = fabController;
            this.messageSender = messageSender;
            this.gameState = gameState;
            this.myParticipantId = myParticipantId;
            this.currentTurnId = gameState.getCurrentTurnNum() - 1;
        }

        @Override protected void onEnterScope(MortarScope scope) {
            bus.register(this);
        }

        @Override protected void onExitScope() {
            bus.unregister(this);
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            updateScreen();
        }

        @Subscribe public void subscribeAcknowledgeVoteEvent(AcknowledgeVoteMessage.Event avm) {
            getView().notifyDataSetChanged();
        }

        @Subscribe public void subscribeVoteFinishedEvent(VoteFinishedMessage.Event vfe) {
            toolbarController.setTitle(R.string.team_vote);
            toolbarController.setState(false);
            teamVote.parseFinishedMessage(vfe);
            getView().showResults(true);
            if (!teamVote.getAllVotes().containsKey(myParticipantId)) {
                toolbarController.setState(true);
                fabController.show(new Runnable() {
                    @Override public void run() {
                        flow.goBack();
                    }
                });
                return;
            }
            updateScreen();
        }

        @Subscribe
        public void subscribeTeamVoteApprovalUpdateEvent(TeamVoteApprovalUpdateMessage.Event mvaue) {
            updateScreen();
        }

        public void updateScreen() {
            getView().setVoteEnable(false);
            if (teamVote.isCompleted()) {
                getView().showResults(true);
            } else {
                getView().showResults(false);
            }
            if (teamVote.isNullified()) {
                getView().setNullified(gameState.getPlayerByParticipant(teamVote.getPlayer()).getNick());
            }
            if (teamVote.isCompleted()) {
                if ((teamVote.isApproved() || teamVote.isNullified())) {
                    toolbarController.setTeamVoteResult(gameState.getTurn(currentTurnId).getIndexOfVote(teamVote) + 1, teamVote.getVerdict());
                    toolbarController.setState(true);
                    if (teamVote.isApproved()) {
                        getView().setVoteApproved();
                    }
                    fabController.show(new Runnable() {
                        @Override public void run() {
                            if (gameState.isGameCompleted()) {
                                flow.goTo(new WinnerScreen());
                            } else if (teamVote.getVerdict()) {
                                MissionVote missionVote = gameState.getTurn(currentTurnId).getMissionVote();
                                int cardNumber = gameState.getCardNumber(InTheSpotlightCard.class);
                                flow.goTo(cardNumber > 0 ? new InTheSpotlightScreen(missionVote) : new MissionVoteScreen(missionVote));
                            } else {
                                toolbarController.setCurrentLeader(gameState.getCurrentLeader());
                                flow.goTo(new ChooseTeamScreen());
                            }
                        }
                    });
                } else {
                    getView().setVoteRemaining(teamVote.getApprovalNeeded() - teamVote.getApprovalGathered());
                    if (!voted) {
                        boolean myConfidenceCard = false;
                        for (PlotCard plotCard : gameState.getPlayerByParticipant(myParticipantId).getCardHand()) {
                            if (plotCard instanceof NoConfidenceCard) {
                                myConfidenceCard = true;
                                break;
                            }
                        }
                        toolbarController.setState(myConfidenceCard);
                        if (myConfidenceCard) {
                            getView().setVoteEnable(true);
                        }
                    } else {
                        toolbarController.setState(false);
                    }
                }
            }
        }

        public void onNullSelected() {
            voted = true;
            messageSender.sendMessage(new NoConfidenceMessage(false));
            getView().setVoteEnable(false);
        }

        public void onApproveButton() {
            voted = true;
            messageSender.sendMessage(new NoConfidenceMessage(true));
            getView().setVoteEnable(false);
        }
    }
}

