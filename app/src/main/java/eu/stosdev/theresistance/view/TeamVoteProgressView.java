package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.screen.TeamVoteProgressScreen;
import mortar.Mortar;

public class TeamVoteProgressView extends RelativeLayout {

    @Inject TeamVoteProgressScreen.Presenter presenter;
    @Inject TeamVoteProgressAdapter adapter;

    @InjectView(R.id.team_list) ListView listView;
    @InjectView(R.id.vote_bar) LinearLayout voteBar;
    @InjectView(R.id.nullified_label) TextView nullifiedLabel;
    @InjectView(R.id.bla_container) RelativeLayout layout;

    public TeamVoteProgressView(Context context) {
        this(context, null);
    }

    public TeamVoteProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Mortar.inject(context, this);
        }
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            ButterKnife.inject(this);
            listView.setAdapter(adapter);
            presenter.takeView(this);
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void showResults(boolean show) {
        adapter.showResults(show);
    }

    public void setVoteEnable(boolean enable) {
        voteBar.setVisibility(enable ? VISIBLE : INVISIBLE);
    }

    public void setVoteRemaining(int vote) {
        layout.setVisibility(VISIBLE);
        CharSequence text = getContext().getText(R.string.remaining_approvals);
        nullifiedLabel.setText(text + String.valueOf(vote));
    }

    @OnClick(R.id.null_button)
    public void onNullButton() {
        presenter.onNullSelected();
    }

    @OnClick(R.id.approve_button)
    public void onApproveButton() {
        presenter.onApproveButton();
    }

    public void setNullified(String player) {
        CharSequence text = getContext().getText(R.string.nullified_by);
        nullifiedLabel.setText(text + player);
    }

    public void setVoteApproved() {
        nullifiedLabel.setText("");
    }
}
