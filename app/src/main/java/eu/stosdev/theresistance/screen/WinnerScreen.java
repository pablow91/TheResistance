package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.event.EndGameEvent;
import eu.stosdev.theresistance.event.LocalEvent;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.FabController;
import eu.stosdev.theresistance.view.ToolbarController;
import eu.stosdev.theresistance.view.WinnerView;
import flow.Layout;
import mortar.Blueprint;
import mortar.ViewPresenter;

@Layout(R.layout.winner)
public class WinnerScreen implements Blueprint {

    public WinnerScreen() {
    }

    @Override public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = WinnerView.class, addsTo = GameMainScreen.Module.class)
    public static class Module {
    }

    @Singleton
    public static class Presenter extends ViewPresenter<WinnerView> {

        private final TypedBus<LocalEvent> bus;
        private final GameState gameState;
        private final ToolbarController toolbarController;
        private final FabController fabController;
        private final String myParticipantId;

        @Inject
        public Presenter(TypedBus<LocalEvent> bus, GameState gameState, ToolbarController toolbarController, FabController fabController, @Named("myParticipantId") String myParticipantId) {
            this.bus = bus;
            this.gameState = gameState;
            this.toolbarController = toolbarController;
            this.fabController = fabController;
            this.myParticipantId = myParticipantId;
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            toolbarController.setTitle(R.string.winner);
            fabController.show(new Runnable() {
                @Override public void run() {
                    toolbarController.resetState();
                    bus.post(new EndGameEvent());
                }
            });
            getView().setWinner(gameState.getWinner());
            Player.Type myType = gameState.getPlayerTypeMap().get(gameState.getPlayerByParticipant(myParticipantId));
            toolbarController.setState(myType.equals(gameState.getWinner()));
        }
    }
}

