package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.card.KeepingCloseEyeCard;
import eu.stosdev.theresistance.model.card.PlotCard;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.MissionVote;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.model.messages.AcknowledgeVoteMessage;
import eu.stosdev.theresistance.model.messages.VoteFinishedMessage;
import eu.stosdev.theresistance.model.messages.VoteResponseMessage;
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.utils.MessageSender;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.MissionVoteView;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Layout(R.layout.mission_vote)
public class MissionVoteScreen implements Blueprint {

    private final MissionVote missionVote;

    public MissionVoteScreen(MissionVote missionVote) {
        this.missionVote = missionVote;
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = MissionVoteView.class, addsTo = GameMainScreen.Module.class)
    public class Module {
        @Provides @Singleton MissionVote provideMissionVote() {
            return missionVote;
        }
    }

    @Singleton
    public static class Presenter extends ViewPresenter<MissionVoteView> {

        private final MissionVote missionVote;
        private final String myParticipantId;
        private final Flow flow;
        private final MessageSender messageSender;
        private final TypedBus<AbsEvent> bus;
        private final GameState gameState;
        private final ToolbarController toolbarController;

        @Inject
        public Presenter(MessageSender messageSender, MissionVote missionVote, @Named("myParticipantId") String myParticipantId, @Named("game") Flow flow, TypedBus<AbsEvent> bus, GameState gameState, ToolbarController toolbarController) {
            this.missionVote = missionVote;
            this.myParticipantId = myParticipantId;
            this.flow = flow;
            this.messageSender = messageSender;
            this.bus = bus;
            this.gameState = gameState;
            this.toolbarController = toolbarController;
        }

        @Override protected void onEnterScope(MortarScope scope) {
            bus.register(this);
        }

        @Override protected void onExitScope() {
            bus.unregister(this);
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            getView().setMissionVote(missionVote);
            getView().setFailButtonEnable(gameState.getPlayerTypeMap().get(gameState.getPlayerByParticipant(myParticipantId)).equals(Player.Type.SPY));
            toolbarController.setTitle(R.string.mission_vote);
            onVoteFinished();
            if (missionVote.getVoters().contains(myParticipantId)) {
                getView().activateVoting();
            } else {
                getView().disableVoting();
                toolbarController.setState(false);
            }
        }

        @Subscribe public void subscribeAcknowledgeVoteMessage(AcknowledgeVoteMessage.Event avm) {
            getView().notifyDataSetChanged();
        }

        @Subscribe public void subscribeVoteFinishedEvent(VoteFinishedMessage.Event vfe) {
            missionVote.parseFinishedMessage(vfe);
            onVoteFinished();
        }

        private void onVoteFinished() {
            if (missionVote.isCompleted()) {
                List<PlotCard> cardHand = gameState.getPlayerByParticipant(myParticipantId).getCardHand();
                boolean haveKeepCard = false;
                for (PlotCard plotCard : cardHand) {
                    if (plotCard instanceof KeepingCloseEyeCard) {
                        haveKeepCard = true;
                    }
                }
                flow.goTo(haveKeepCard ? new KeepingCloseEyeScreen(missionVote) : new MissionResultScreen(missionVote));
            }
        }

        public void onVote(boolean vote) {
            toolbarController.setState(false);
            messageSender.sendMessage(new VoteResponseMessage(vote));
            getView().disableVoting();
        }

    }
}

