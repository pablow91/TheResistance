package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;

import eu.stosdev.theresistance.screen.OverheardConversationScreen;
import mortar.Mortar;

public class OverheardConversationView extends SelectOnePlayerListView<OverheardConversationScreen.Presenter> {
    public OverheardConversationView(Context context, AttributeSet attrs) {
        super(context, attrs, Mortar.getScope(context).getObjectGraph().get(OverheardConversationScreen.Presenter.class));
    }
}
