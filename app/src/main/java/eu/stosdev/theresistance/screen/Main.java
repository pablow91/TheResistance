package eu.stosdev.theresistance.screen;

import android.content.Context;
import android.os.Bundle;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Provides;
import eu.stosdev.theresistance.main.App;
import eu.stosdev.theresistance.utils.DpiConverter;
import eu.stosdev.theresistance.utils.FlowOwner;
import eu.stosdev.theresistance.view.FabController;
import eu.stosdev.theresistance.view.GameProgressView;
import eu.stosdev.theresistance.view.MainView;
import eu.stosdev.theresistance.view.ToolbarController;
import eu.stosdev.theresistance.view.TurnProgressView;
import flow.Flow;
import flow.Parcer;
import mortar.Blueprint;

public class Main implements Blueprint {

    private final Context context;

    public Main(Context context) {
        this.context = context;
    }

    @Override
    public String getMortarScopeName() {
        return getClass().getName();
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @dagger.Module(injects = {MainView.class, GameProgressView.class, TurnProgressView.class}, addsTo = App.ApplicationModule.class, library = true)
    public class Module {
        @Provides @Singleton @Named("main") Flow provideFlow(Presenter presenter) {
            return presenter.getFlow();
        }

        @Provides @Singleton ToolbarController provideToolbarController(DpiConverter dpiConverter) {
            return new ToolbarController(dpiConverter);
        }

        @Provides @Singleton FabController provideFabController(DpiConverter dpiConverter) {
            return new FabController(dpiConverter);
        }

        @Provides @Singleton Context provideContext() {
            return context;
        }
    }

    @Singleton
    public static class Presenter extends FlowOwner<Blueprint, MainView> {

        private final ToolbarController toolbarController;
        private final FabController fabController;

        @Inject
        Presenter(Parcer<Object> flowParcer, ToolbarController toolbarController, FabController fabController) {
            super(flowParcer);
            this.toolbarController = toolbarController;
            this.fabController = fabController;
        }

        @Override
        protected Blueprint getFirstScreen() {
            return new StartScreen();
        }

        @Override public void onLoad(Bundle savedInstanceState) {
            fabController.setFab(getView().getFab());
            toolbarController.setToolbar(getView().getToolbar());
            toolbarController.loadState(savedInstanceState);
            super.onLoad(savedInstanceState);
        }

        @Override public void onSave(Bundle outState) {
            super.onSave(outState);
            toolbarController.saveState(outState);
        }
    }
}
