package eu.stosdev.theresistance.utils;

import android.os.Bundle;
import android.view.View;

import flow.Backstack;
import flow.Flow;
import flow.Parcer;
import mortar.Blueprint;
import mortar.ViewPresenter;

/**
 * Base class for all presenters that manage a {@link flow.Flow}.
 */
public abstract class FlowOwner<S extends Blueprint, V extends View & CanShowScreen<S>>
        extends ViewPresenter<V> implements Flow.Listener {

    private static final String FLOW_KEY = "FLOW_KEY";

    private final Parcer<Object> parcer;

    private Flow flow;

    protected FlowOwner(Parcer<Object> parcer) {
        this.parcer = parcer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoad(Bundle savedInstanceState) {
        super.onLoad(savedInstanceState);

        if (flow == null) {
            Backstack backstack;

            if (savedInstanceState != null) {
                backstack = Backstack.from(savedInstanceState.getParcelable(FLOW_KEY), parcer);
            } else {
                backstack = Backstack.fromUpChain(getFirstScreen());
            }

            flow = new Flow(backstack, this);
        }
        showScreen((S) flow.getBackstack().current().getScreen(), null);
    }

    @Override
    public void onSave(Bundle outState) {
        super.onSave(outState);
        outState.putParcelable(FLOW_KEY, flow.getBackstack().getParcelable(parcer));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void go(Backstack backstack, Flow.Direction flowDirection, Flow.Callback callback) {
        S newScreen = (S) backstack.current().getScreen();
        showScreen(newScreen, flowDirection);
        callback.onComplete();
    }

    protected void showScreen(S newScreen, Flow.Direction flowDirection) {
        V view = getView();
        if (view == null) return;

        view.showScreen(newScreen, flowDirection);
    }

    public final Flow getFlow() {
        return flow;
    }

    /**
     * Returns the first screen shown by this presenter.
     */
    protected abstract S getFirstScreen();
}