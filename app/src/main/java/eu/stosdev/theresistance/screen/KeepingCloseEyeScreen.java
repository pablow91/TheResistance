package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import com.google.common.base.Predicate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.MissionVote;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.model.messages.KeepingCloseEyeMessage;
import eu.stosdev.theresistance.utils.MessageSender;
import eu.stosdev.theresistance.view.FabController;
import eu.stosdev.theresistance.view.KeepingCloseEyeView;
import eu.stosdev.theresistance.view.OnPlayerSelected;
import eu.stosdev.theresistance.view.SelectOnePlayerListView;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;

@Layout(R.layout.keeping_close_eye_view)
public class KeepingCloseEyeScreen implements Blueprint {

    private final MissionVote missionVote;

    public KeepingCloseEyeScreen(MissionVote missionVote) {
        this.missionVote = missionVote;
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = {KeepingCloseEyeView.class, Presenter.class}, addsTo = GameMainScreen.Module.class)
    public class Module {
        @Provides @Singleton MissionVote provideMissionVote() {
            return missionVote;
        }
    }

    @Singleton
    public static class Presenter extends ViewPresenter<SelectOnePlayerListView<Presenter>> implements OnPlayerSelected, Predicate<Player> {

        private final GameState gameState;
        private final Flow flow;
        private final String myParticipantId;
        private final MessageSender messageSender;
        private final FabController fabController;
        private final MissionVote missionVote;

        @Inject
        public Presenter(GameState gameState, @Named("game") Flow flow, @Named("myParticipantId") String myParticipantId, MessageSender messageSender, FabController fabController, MissionVote missionVote) {
            this.gameState = gameState;
            this.flow = flow;
            this.myParticipantId = myParticipantId;
            this.messageSender = messageSender;
            this.fabController = fabController;
            this.missionVote = missionVote;
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            getView().setPlayerList(gameState.getFilteredPlayerList(this));
            fabController.show(new Runnable() {
                @Override public void run() {
                    flow.goTo(new MissionResultScreen(missionVote));
                }
            });
        }

        @Override public void onPlayerSelected(Player player) {
            messageSender.sendMessage(new KeepingCloseEyeMessage());
            getView().setResult(missionVote.getAllVotes().get(player.getParticipantId()));
            fabController.hide();
            flow.goTo(new MissionResultScreen(missionVote));
        }

        @Override public boolean apply(Player input) {
            return missionVote.getVoters().contains(input.getParticipantId()) && !input.getParticipantId().equals(myParticipantId);
        }
    }
}