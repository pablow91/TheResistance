package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.game.TeamVote;
import eu.stosdev.theresistance.model.messages.VoteResponseMessage;
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.utils.MessageSender;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.TeamVoteView;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Layout(R.layout.team_vote)
public class TeamVoteScreen implements Blueprint {
    private final TeamVote currentVote;

    public TeamVoteScreen(TeamVote currentVote) {
        this.currentVote = currentVote;
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = TeamVoteView.class, addsTo = GameMainScreen.Module.class)
    public class Module {
        @Provides @Singleton TeamVote providesTeamVote() {
            return currentVote;
        }
    }

    @Singleton
    public static class Presenter extends ViewPresenter<TeamVoteView> {
        private final Flow flow;
        private final TeamVote currentVote;
        private final MessageSender messageSender;
        private final TypedBus<AbsEvent> bus;
        private final String myParticipantId;
        private final ToolbarController toolbarController;

        @Inject
        public Presenter(TeamVote currentVote, @Named("game") Flow flow, MessageSender messageSender, TypedBus<AbsEvent> bus, @Named("myParticipantId") String myParticipantId, ToolbarController toolbarController) {
            this.currentVote = currentVote;
            this.flow = flow;
            this.messageSender = messageSender;
            this.bus = bus;
            this.myParticipantId = myParticipantId;
            this.toolbarController = toolbarController;
        }

        @Override protected void onEnterScope(MortarScope scope) {
            bus.register(this);
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            toolbarController.setState(true);
            toolbarController.setTitle(R.string.team_vote);
            if (!currentVote.getVoters().contains(myParticipantId)) {
                flow.goTo(new TeamVoteProgressScreen(currentVote));
            }
        }

        @Override protected void onExitScope() {
            bus.unregister(this);
        }

        public void onVoteSelected(boolean vote) {
            messageSender.sendMessage(new VoteResponseMessage(vote));
            flow.goTo(new TeamVoteProgressScreen(currentVote));
        }
    }
}
