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
import eu.stosdev.theresistance.view.OverheardConversationView;
import eu.stosdev.theresistance.view.SelectOnePlayerListView;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;

@Layout(R.layout.overheard_conversation_view)
public class OverheardConversationScreen implements Blueprint {

    private final RevealIdentityCard plotCard;

    public OverheardConversationScreen(RevealIdentityCard plotCard) {
        this.plotCard = plotCard;
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = {OverheardConversationView.class, Presenter.class}, addsTo = GameMainScreen.Module.class)
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
        private final int myIndex;
        private final RevealIdentityCard plotCard;

        @Inject
        public Presenter(GameState gameState, @Named("game") Flow flow, @Named("myParticipantId") String myParticipantId, MessageSender messageSender, RevealIdentityCard plotCard) {
            this.gameState = gameState;
            this.flow = flow;
            this.myParticipantId = myParticipantId;
            this.messageSender = messageSender;
            this.plotCard = plotCard;
            this.myIndex = gameState.getPlayerList().indexOf(gameState.getPlayerByParticipant(myParticipantId));
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            getView().setPlayerList(gameState.getFilteredPlayerList(this));
        }

        @Override public void onPlayerSelected(Player player) {
            int i = gameState.getPlayerByParticipant(myParticipantId).getCardHand().indexOf(plotCard);
            messageSender.sendMessage(new OpenUpConfirmationMessage(myParticipantId, i, player.getParticipantId(), myParticipantId));
            plotCard.setFrom(myParticipantId);
            plotCard.setTo(player.getParticipantId());
            flow.goTo(new CardPickingScreen());
        }

        @Override public boolean apply(Player input) {
            int i = gameState.getPlayerList().indexOf(input);
            int leftIndex = (myIndex == 0) ? gameState.getPlayerList().size() - 1 : myIndex - 1;
            int rightIndex = (myIndex + 1) % gameState.getPlayerList().size();
            return rightIndex == i || leftIndex == i;
        }

    }
}
