package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;

import eu.stosdev.theresistance.screen.KeepingCloseEyeScreen;
import mortar.Mortar;

public class KeepingCloseEyeView extends SelectOnePlayerListView<KeepingCloseEyeScreen.Presenter> {
    public KeepingCloseEyeView(Context context, AttributeSet attrs) {
        super(context, attrs, Mortar.getScope(context).getObjectGraph().get(KeepingCloseEyeScreen.Presenter.class));
    }
}