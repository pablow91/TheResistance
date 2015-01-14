package eu.stosdev.theresistance.view;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.screen.Main;
import eu.stosdev.theresistance.utils.CanShowScreen;
import eu.stosdev.theresistance.utils.ScreenConductor;
import flow.Flow;
import mortar.Blueprint;
import mortar.Mortar;

public class MainView extends RelativeLayout implements CanShowScreen<Blueprint> {

    @Inject Main.Presenter presenter;
    @InjectView(R.id.my_awesome_toolbar) Toolbar toolbar;
    @InjectView(R.id.container) FrameLayout container;
    @InjectView(R.id.fab_button) ImageButton imageButton;

    private ScreenConductor<Blueprint> screenMaestro;

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Mortar.inject(context, this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            ButterKnife.inject(this);
            screenMaestro = new ScreenConductor<>(getContext(), container);
            presenter.takeView(this);
        }
    }

    public Flow getFlow() {
        return presenter.getFlow();
    }

    @Override
    public void showScreen(Blueprint screen, Flow.Direction direction) {
        screenMaestro.showScreen(screen, direction);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public ImageButton getFab() {
        return imageButton;
    }
}
