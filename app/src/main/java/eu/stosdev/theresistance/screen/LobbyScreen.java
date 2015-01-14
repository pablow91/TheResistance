package eu.stosdev.theresistance.screen;

import android.os.Bundle;

import com.squareup.otto.Subscribe;

import java.util.Random;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.messages.InitialDataMessage;
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.LobbyView;
import eu.stosdev.theresistance.view.ToolbarController;
import flow.Flow;
import flow.Layout;
import mortar.Blueprint;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Layout(R.layout.lobby)
public class LobbyScreen implements Blueprint {
    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = LobbyView.class, addsTo = GameMainScreen.Module.class)
    public static class Module {
    }

    @Singleton
    public static class Presenter extends ViewPresenter<LobbyView> {
        private final TypedBus<AbsEvent> bus;
        private final StartTurnDispatcher startTurnDispatcher;
        private final Flow flow;
        private final ToolbarController toolbarController;

        @Inject
        public Presenter(TypedBus<AbsEvent> bus, StartTurnDispatcher startTurnDispatcher, @Named("game") Flow flow, ToolbarController toolbarController) {
            this.bus = bus;
            this.startTurnDispatcher = startTurnDispatcher;
            this.flow = flow;
            this.toolbarController = toolbarController;
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            bus.register(this);
        }

        @Override
        protected void onExitScope() {
            bus.unregister(this);
        }

        @Override protected void onLoad(Bundle savedInstanceState) {
            toolbarController.show();
            toolbarController.setTitle(R.string.lobby);
            int i = new Random().nextInt(7);
            if (i >= 4) {
                getView().showSpyTip(i - 4);
            } else {
                getView().showResistanceTip(i);
            }
        }

        @Subscribe
        public void onMessageReceived(InitialDataMessage.Event event) {
            flow.goTo(startTurnDispatcher.goToNewTurn());
        }
    }
}
