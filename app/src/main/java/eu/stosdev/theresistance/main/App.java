package eu.stosdev.theresistance.main;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import eu.stosdev.theresistance.BuildConfig;
import eu.stosdev.theresistance.event.LocalEvent;
import eu.stosdev.theresistance.model.messages.utils.AbsEvent;
import eu.stosdev.theresistance.utils.DpiConverter;
import eu.stosdev.theresistance.utils.GsonParcer;
import eu.stosdev.theresistance.utils.TypedBus;
import flow.Parcer;
import mortar.Mortar;
import mortar.MortarScope;


public class App extends Application {
    private MortarScope applicationScope;

    @Override
    public void onCreate() {
        super.onCreate();
        // Eagerly validate development builds (too slow for production).
        applicationScope = Mortar.createRootScope(BuildConfig.DEBUG, ObjectGraph.create(new ApplicationModule()));
    }

    @Override
    public Object getSystemService(String name) {
        if (Mortar.isScopeSystemService(name)) {
            return applicationScope;
        }
        return super.getSystemService(name);
    }

    @Module(injects = MainActivity.class, library = true)
    public class ApplicationModule {
        @Provides @Singleton Gson provideGson() {
            return new GsonBuilder().create();
        }

        @Provides @Singleton Parcer<Object> provideParcer(Gson gson) {
            return new GsonParcer<>(gson);
        }

        @Provides @Singleton TypedBus<LocalEvent> provideLocalBus() {
            return new TypedBus<>();
        }

        @Provides @Singleton TypedBus<AbsEvent> provideGameEventBus() {
            return new TypedBus<>();
        }

        @Provides @Singleton DpiConverter provideDpiConverter() {
            return new DpiConverter(App.this);
        }
    }


}
