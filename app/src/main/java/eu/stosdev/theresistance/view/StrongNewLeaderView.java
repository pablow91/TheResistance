package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.screen.StrongNewLeaderScreen;
import mortar.Mortar;

public class StrongNewLeaderView extends RelativeLayout {

    @Inject StrongNewLeaderScreen.Presenter presenter;

    @InjectView(R.id.new_leader) TextView newLeader;

    public StrongNewLeaderView(Context context) {
        this(context, null);
    }

    public StrongNewLeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Mortar.inject(context, this);
        }
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            ButterKnife.inject(this);
            presenter.takeView(this);
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    public void setNewLeader(String name) {
        newLeader.setText(name);
    }
}
