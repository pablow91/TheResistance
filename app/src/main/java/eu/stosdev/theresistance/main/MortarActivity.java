package eu.stosdev.theresistance.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;

import eu.stosdev.theresistance.screen.Main;
import mortar.Mortar;
import mortar.MortarActivityScope;
import mortar.MortarScope;

public abstract class MortarActivity extends ActionBarActivity {
    private MortarActivityScope activityScope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MortarScope parentScope = Mortar.getScope(getApplication());
        activityScope = Mortar.requireActivityScope(parentScope, new Main(this));
        activityScope.onCreate(savedInstanceState);
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Mortar.isScopeSystemService(name)) {
            return activityScope;
        }
        return super.getSystemService(name);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        activityScope.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            MortarScope parentScope = Mortar.getScope(getApplication());
            parentScope.destroyChild(activityScope);
            activityScope = null;
        }
    }
}
