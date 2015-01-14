package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.card.RevealIdentityCard;
import eu.stosdev.theresistance.model.card.TakeResponsibilityCard;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.model.messages.RevealIdentityAcknowledgeMessage;
import eu.stosdev.theresistance.model.messages.RevealIdentityMessage;
import eu.stosdev.theresistance.model.messages.TakeResponsibilityMessage;
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.FabController;
import eu.stosdev.theresistance.view.InstantCardSummaryView;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Layout(R.layout.instant_card_summary_view)
public class InstantCardSummaryScreen implements Blueprint {

    private final RevealIdentityCard plotCard;

    public InstantCardSummaryScreen(RevealIdentityCard plotCard) {
        this.plotCard = plotCard;
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = InstantCardSummaryView.class, addsTo = GameMainScreen.Module.class)
    public class Module {
        @Provides @Singleton RevealIdentityCard providesPlotCard() {
            return plotCard;
        }
    }

    @Singleton
    public static class Presenter extends ViewPresenter<InstantCardSummaryView> {

        private final RevealIdentityCard plotCard;
        private final ToolbarController toolbarController;
        private final Flow flow;
        private final TypedBus<AbsEvent> bus;
        private final FabController fabController;
        private final GameState gameState;

        @Inject
        public Presenter(RevealIdentityCard plotCard, ToolbarController toolbarController, @Named("game") Flow flow, TypedBus<AbsEvent> bus, FabController fabController, GameState gameState) {
            this.plotCard = plotCard;
            this.toolbarController = toolbarController;
            this.flow = flow;
            this.bus = bus;
            this.fabController = fabController;
            this.gameState = gameState;
        }

        @Override protected void onEnterScope(MortarScope scope) {
            bus.register(this);
        }

        @Override protected void onExitScope() {
            bus.unregister(this);
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            toolbarController.setState(false);
            toolbarController.setTitle(R.string.summary);
            updateView();
        }

        @Subscribe
        public void subscribeRevealIdentityAcknowledgeMessage(RevealIdentityAcknowledgeMessage.Event ria) {
            updateView();
        }

        @Subscribe public void subscribeRevealIdentityEvent(RevealIdentityMessage.Event rim) {
            updateView();
        }

        @Subscribe public void subscribeRevealIdentityEvent(TakeResponsibilityMessage.Event trim) {
            updateView();
        }

        private void updateView() {
            if (plotCard instanceof TakeResponsibilityCard) {
                TakeResponsibilityCard takeCard = (TakeResponsibilityCard) plotCard;
                if (takeCard.isUnused()) {
                    if (plotCard.getFrom() != null) {
                        Player.Type type = gameState.getPlayerTypeMap().get(gameState.getPlayerByParticipant(plotCard.getFrom()));
                        getView().setTakenCard(takeCard.getPlotCardClass());
                        getView().setTaker(gameState.getPlayerByParticipant(plotCard.getFrom()).getNick(), type, R.string.taken_card);
                    }
                    if (plotCard.getTo() != null) {
                        Player.Type type = gameState.getPlayerTypeMap().get(gameState.getPlayerByParticipant(plotCard.getTo()));
                        getView().setReceiver(gameState.getPlayerByParticipant(plotCard.getTo()).getNick(), type);
                    }
                }

                if (takeCard.isUnused() || (takeCard.getFrom() != null && takeCard.getTo() != null)) {
                    fabController.show(new Runnable() {
                        @Override public void run() {
                            flow.goTo(new CardPickingScreen());
                        }
                    });
                }
            } else {
                if (plotCard.getFrom() != null) {
                    Player.Type type = gameState.getPlayerTypeMap().get(gameState.getPlayerByParticipant(plotCard.getFrom()));
                    getView().setTaker(gameState.getPlayerByParticipant(plotCard.getFrom()).getNick(), type, R.string.see_identity);

                }
                if (plotCard.getTo() != null) {
                    Player.Type type = gameState.getPlayerTypeMap().get(gameState.getPlayerByParticipant(plotCard.getTo()));
                    getView().setReceiver(gameState.getPlayerByParticipant(plotCard.getTo()).getNick(), type);
                }
                if (plotCard.getFrom() != null && plotCard.getTo() != null) {
                    fabController.show(new Runnable() {
                        @Override public void run() {
                            flow.goTo(new CardPickingScreen());
                        }
                    });
                }
            }
        }

    }
}

