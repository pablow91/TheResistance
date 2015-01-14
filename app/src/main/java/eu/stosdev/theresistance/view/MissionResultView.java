package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.screen.MissionResultScreen;
import mortar.Mortar;

public class MissionResultView extends RelativeLayout {

    @Inject MissionResultScreen.Presenter presenter;

    @InjectView(R.id.for_votes) TextView forVotes;
    @InjectView(R.id.against_votes) TextView againstVotes;
    @InjectView(R.id.mission_status) TextView missionStatus;

    public MissionResultView(Context context) {
        this(context, null);
    }

    public MissionResultView(Context context, AttributeSet attrs) {
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

    public void setResults(int approved, int against, boolean success) {
        forVotes.setText(String.valueOf(approved));
        againstVotes.setText(String.valueOf(against));
        missionStatus.setText(success ? R.string.success : R.string.failure);
    }

    public void setPublic(String nick, boolean result) {

    }
}
