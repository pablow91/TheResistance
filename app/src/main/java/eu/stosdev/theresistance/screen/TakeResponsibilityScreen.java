package eu.stosdev.theresistance.screen;

import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.card.PlotCard;
import eu.stosdev.theresistance.model.card.TakeResponsibilityCard;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.model.messages.TakeResponsibilityMessage;
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.utils.MessageSender;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.CardController;
import eu.stosdev.theresistance.view.FabController;
import eu.stosdev.theresistance.view.TakeResponsibilityView;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Layout(R.layout.take_responsibility_view)
public class TakeResponsibilityScreen implements Blueprint {

    private final TakeResponsibilityCard plotCard;

    public TakeResponsibilityScreen(TakeResponsibilityCard plotCard) {
        this.plotCard = plotCard;
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = TakeResponsibilityView.class, addsTo = GameMainScreen.Module.class)
    public class Module {
        @Provides @Singleton public TakeResponsibilityCard provideTakeResponsibilityCard() {
            return plotCard;
        }
    }

    @Singleton
    public static class Presenter extends ViewPresenter<TakeResponsibilityView> implements CardController.OnCardSelectedListener {

        private final CardController cardController;
        private final String myParticipantId;
        private final FabController fabController;
        private final MessageSender messageSender;
        private final GameState gameState;
        private final Flow flow;
        private final TypedBus<AbsEvent> bus;
        private final TakeResponsibilityCard takeResponsibilityCard;

        @Inject
        public Presenter(CardController cardController, @Named("myParticipantId") String myParticipantId, FabController fabController, MessageSender messageSender, GameState gameState, @Named("game") Flow flow, TypedBus<AbsEvent> bus, TakeResponsibilityCard takeResponsibilityCard) {
            this.cardController = cardController;
            this.myParticipantId = myParticipantId;
            this.fabController = fabController;
            this.messageSender = messageSender;
            this.gameState = gameState;
            this.flow = flow;
            this.bus = bus;
            this.takeResponsibilityCard = takeResponsibilityCard;
        }

        @Override protected void onEnterScope(MortarScope scope) {
            bus.register(this);
        }

        @Override protected void onExitScope() {
            cardController.setOnCardSelectedListener(null);
            bus.unregister(this);
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            List<PlotCard> allCards = cardController.getAllCards();
            boolean isOtherPlayerCard = false;
            for (PlotCard card : allCards) {
                if (!card.getOwner().equals(myParticipantId)) {
                    isOtherPlayerCard = true;
                    break;
                }
            }
            if (isOtherPlayerCard) {
                cardController.setOnCardSelectedListener(this);
            } else {
                getView().showNoCardError();
                fabController.show(new Runnable() {
                    @Override public void run() {
                        int i = gameState.getPlayerByParticipant(myParticipantId).getCardHand().indexOf(takeResponsibilityCard);
                        messageSender.sendMessage(new TakeResponsibilityMessage(null, myParticipantId, -1, i, true));
                        flow.goTo(new InstantCardSummaryScreen(takeResponsibilityCard));
                    }
                });
            }
        }

        @Override public void onCardSelected(PlotCard plotCard) {
            if (!plotCard.getOwner().equals(myParticipantId)) {
                Player player = gameState.getPlayerByParticipant(plotCard.getOwner());
                int cardId = player.getCardHand().indexOf(plotCard);
                int i = gameState.getPlayerByParticipant(myParticipantId).getCardHand().indexOf(takeResponsibilityCard);
                messageSender.sendMessage(new TakeResponsibilityMessage(player.getParticipantId(), myParticipantId, cardId, i, true));
                flow.goTo(new InstantCardSummaryScreen(takeResponsibilityCard));
            } else {
                Toast.makeText(getView().getContext(), R.string.cannot_take_own_card, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
