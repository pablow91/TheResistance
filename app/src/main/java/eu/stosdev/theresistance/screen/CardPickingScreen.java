package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.card.EstablishConfidenceCard;
import eu.stosdev.theresistance.model.card.InstantPlotCard;
import eu.stosdev.theresistance.model.card.PlotCard;
import eu.stosdev.theresistance.model.card.RevealIdentityCard;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Turn;
import eu.stosdev.theresistance.model.messages.AssignPlotCardMessage;
import eu.stosdev.theresistance.model.messages.DrawPlotCardMessage;
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.utils.MessageSender;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.CardPickingView;
import eu.stosdev.theresistance.view.FabController;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Layout(R.layout.card_picking)
public class CardPickingScreen implements Blueprint {

    public CardPickingScreen() {
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = CardPickingView.class, addsTo = GameMainScreen.Module.class)
    public static class Module {
    }

    @Singleton
    public static class Presenter extends ViewPresenter<CardPickingView> {

        private final GameState gameState;
        private final TypedBus<AbsEvent> bus;
        private final String myParticipantId;
        private final ToolbarController toolbarController;
        private final FabController fabController;
        private final Flow flow;
        private final MessageSender messageSender;
        private final Turn currentTurn;

        @Inject
        public Presenter(GameState gameState, TypedBus<AbsEvent> bus, @Named("myParticipantId") String myParticipantId, ToolbarController toolbarController, FabController fabController, @Named("game") Flow flow, MessageSender messageSender) {
            this.gameState = gameState;
            this.bus = bus;
            this.myParticipantId = myParticipantId;
            this.toolbarController = toolbarController;
            this.fabController = fabController;
            this.flow = flow;
            this.messageSender = messageSender;
            this.currentTurn = gameState.getCurrentTurn();
        }

        @Override protected void onEnterScope(MortarScope scope) {
            bus.register(this);
        }

        @Override protected void onExitScope() {
            bus.unregister(this);
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            toolbarController.setTitle(R.string.card_picking);
            toolbarController.setState(gameState.getCurrentLeader().getParticipantId().equals(myParticipantId));
            getView().setCardList(currentTurn.getPickedCards());
            checkedIfFinished();
        }

        @Subscribe public void subscribeDrawPloCardEvent(DrawPlotCardMessage.Event dpce) {
            getView().notifyDataChangeSet();
            checkedIfFinished();
        }

        @Subscribe public void subscribeAssignPlotCardMessage(AssignPlotCardMessage.Event apce) {
            PlotCard plotCard = gameState.getCurrentTurn().getPickedCards().get(apce.getPlotCard());
            getView().checkItem(plotCard);
            if (plotCard.getType() == PlotCard.Type.INSTANT) {
                if (plotCard.getOwner().equals(myParticipantId)) {
                    flow.goTo(((InstantPlotCard) plotCard).takeAction());
                } else {
                    flow.goTo(new InstantCardSummaryScreen((RevealIdentityCard) plotCard));
                }
            } else {
                checkedIfFinished();
            }
        }

        private void checkedIfFinished() {
            if (currentTurn.getCurrentState() != Turn.State.CARD_PICKING) {
                fabController.show(new Runnable() {
                    @Override public void run() {
                        flow.goTo(new ChooseTeamScreen());
                    }
                });
            }
        }

        public void onCardSelected(int position) {
            PlotCard plotCard = currentTurn.getPickedCards().get(position);
            if (plotCard.getOwner() == null) {
                if (myParticipantId.equals(gameState.getCurrentLeader().getParticipantId())) {
                    if (plotCard instanceof EstablishConfidenceCard) {
                        messageSender.sendMessage(new AssignPlotCardMessage(position, gameState.getCurrentLeader().getParticipantId()));
                    } else {
                        flow.goTo(new SelectPlayerForCardScreen(position, plotCard));
                    }
                }
            } else if (plotCard.getType() == PlotCard.Type.INSTANT) {
                flow.goTo(new InstantCardSummaryScreen((RevealIdentityCard) plotCard));
            }
        }
    }
}

