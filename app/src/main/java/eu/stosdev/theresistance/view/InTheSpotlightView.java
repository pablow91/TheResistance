package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;

import eu.stosdev.theresistance.screen.InTheSpotlightScreen;
import mortar.Mortar;

public class InTheSpotlightView extends SelectOnePlayerListView<InTheSpotlightScreen.Presenter> {
    public InTheSpotlightView(Context context, AttributeSet attrs) {
        super(context, attrs, Mortar.getScope(context).getObjectGraph().get(InTheSpotlightScreen.Presenter.class));
    }
}