package eu.stosdev.theresistance.screen;

import android.content.Intent;
import android.net.Uri;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.event.ActivateStartButtonEvent;
import eu.stosdev.theresistance.event.InvitationListEvent;
import eu.stosdev.theresistance.event.LocalEvent;
import eu.stosdev.theresistance.event.StartGameEvent;
import eu.stosdev.theresistance.utils.TypedBus;
import eu.stosdev.theresistance.view.StartView;
import flow.Layout;
import mortar.Blueprint;
import mortar.MortarScope;
import mortar.ViewPresenter;

@Layout(R.layout.main)
public class StartScreen implements Blueprint {
    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = StartView.class, addsTo = Main.Module.class)
    public static class Module {
    }

    @Singleton
    public static class Presenter extends ViewPresenter<StartView> {
        private final TypedBus<LocalEvent> bus;

        @Inject
        public Presenter(TypedBus<LocalEvent> bus) {
            this.bus = bus;
        }

        @Override protected void onEnterScope(MortarScope scope) {
            bus.register(this);
        }

        @Override protected void onExitScope() {
            bus.unregister(this);
        }

        public void onGameStart() {
            getView().blockElements();
            bus.post(new StartGameEvent());
        }

        public void onInvitationList() {
            bus.post(new InvitationListEvent());
        }

        @Subscribe public void subscribeActivateButtonEvent(ActivateStartButtonEvent event) {
            getView().unblockElements();
        }

        public void showRules() {
            getView().getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=ZuyuHmUdA7k")));
        }
    }

}
