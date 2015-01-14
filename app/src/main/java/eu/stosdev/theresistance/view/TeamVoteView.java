package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import android.widget.RelativeLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.screen.TeamVoteScreen;
import mortar.Mortar;

public class TeamVoteView extends RelativeLayout {

    @Inject TeamVoteScreen.Presenter presenter;
    @Inject TeamVoteListAdapter adapter;

    @InjectView(R.id.team_list) ListView listView;

    public TeamVoteView(Context context) {
        this(context, null);
    }

    public TeamVoteView(Context context, AttributeSet attrs) {
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
            listView.setAdapter(adapter);
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    @OnClick(R.id.no_vote)
    public void onNoVote() {
        presenter.onVoteSelected(false);
    }

    @OnClick(R.id.yes_vote)
    public void onYesVote() {
        presenter.onVoteSelected(true);
    }
}
