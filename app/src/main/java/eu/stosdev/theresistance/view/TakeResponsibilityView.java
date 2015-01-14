package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.screen.TakeResponsibilityScreen;
import mortar.Mortar;

public class TakeResponsibilityView extends RelativeLayout {

    @Inject TakeResponsibilityScreen.Presenter presenter;
    @InjectView(R.id.take_responsibility_label) TextView textView;

    public TakeResponsibilityView(Context context) {
        this(context, null);
    }

    public TakeResponsibilityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Mortar.inject(context, this);
        }
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        if (!isInEditMode()) {
            presenter.takeView(this);
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    public void showNoCardError() {
        textView.setText(R.string.no_card_available);
    }
}
