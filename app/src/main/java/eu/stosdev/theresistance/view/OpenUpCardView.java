package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;

import eu.stosdev.theresistance.screen.RevealMyIdentityScreen;
import mortar.Mortar;

public class OpenUpCardView extends SelectOnePlayerListView<RevealMyIdentityScreen.Presenter> {
    public OpenUpCardView(Context context, AttributeSet attrs) {
        super(context, attrs, Mortar.getScope(context).getObjectGraph().get(RevealMyIdentityScreen.Presenter.class));
    }
}
