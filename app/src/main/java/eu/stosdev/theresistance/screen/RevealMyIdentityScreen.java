package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import com.google.common.base.Predicate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.card.RevealIdentityCard;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.model.messages.OpenUpConfirmationMessage;
import eu.stosdev.theresistance.utils.MessageSender;
import eu.stosdev.theresistance.view.OnPlayerSelected;
import eu.stosdev.theresistance.view.OpenUpCardView;
import eu.stosdev.theresistance.view.SelectOnePlayerListView;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;

@Layout(R.layout.reveal_my_identity_screen)
public class RevealMyIdentityScreen implements Blueprint {

    private final RevealIdentityCard plotCard;

    public RevealMyIdentityScreen(RevealIdentityCard plotCard) {
        this.plotCard = plotCard;
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = {OpenUpCardView.class, Presenter.class}, addsTo = GameMainScreen.Module.class)
    public class Module {
        @Provides @Singleton public RevealIdentityCard providesPlotCard() {
            return plotCard;
        }
    }

    @Singleton
    public static class Presenter extends ViewPresenter<SelectOnePlayerListView<Presenter>> implements OnPlayerSelected, Predicate<Player> {

        private final GameState gameState;
        private final Flow flow;
        private final String myParticipantId;
        private final MessageSender messageSender;
        private final RevealIdentityCard plotCard;
        private final ToolbarController toolbarController;

        @Inject
        public Presenter(GameState gameState, @Named("game") Flow flow, @Named("myParticipantId") String myParticipantId, MessageSender messageSender, RevealIdentityCard plotCard, ToolbarController toolbarController) {
            this.gameState = gameState;
            this.flow = flow;
            this.myParticipantId = myParticipantId;
            this.messageSender = messageSender;
            this.plotCard = plotCard;
            this.toolbarController = toolbarController;
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            getView().setPlayerList(gameState.getFilteredPlayerList(this));
            toolbarController.setState(true);
            toolbarController.setTitle(R.string.reveal_identity_to);
        }

        @Override public void onPlayerSelected(Player player) {
            int i = gameState.getPlayerByParticipant(myParticipantId).getCardHand().indexOf(plotCard);
            messageSender.sendMessage(new OpenUpConfirmationMessage(myParticipantId, i, myParticipantId, player.getParticipantId()));
            plotCard.setFrom(myParticipantId);
            plotCard.setTo(player.getParticipantId());
            flow.goTo(new InstantCardSummaryScreen(plotCard));
        }

        @Override public boolean apply(Player input) {
            return !input.getParticipantId().equals(myParticipantId);
        }
    }
}

