package eu.stosdev.theresistance.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.game.MissionVote;
import eu.stosdev.theresistance.screen.MissionVoteScreen;
import mortar.Mortar;

public class MissionVoteView extends RelativeLayout {

    @Inject MissionVoteScreen.Presenter presenter;
    @Inject MissionVoteListAdapter adapter;

    @InjectView(R.id.team_list) ListView listView;
    @InjectView(R.id.yes_vote) Button yesVote;
    @InjectView(R.id.no_vote) Button noVote;

    public MissionVoteView(Context context) {
        this(context, null);
    }

    public MissionVoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Mortar.inject(context, this);
        }
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            ButterKnife.inject(this);
            yesVote.setVisibility(INVISIBLE);
            noVote.setVisibility(INVISIBLE);
            listView.setAdapter(adapter);
            presenter.takeView(this);
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    public void activateVoting() {
        yesVote.setVisibility(VISIBLE);
        noVote.setVisibility(VISIBLE);

    }

    public void disableVoting() {
        yesVote.setVisibility(GONE);
        noVote.setVisibility(GONE);
    }

    @OnClick(R.id.yes_vote)
    public void onYesVote() {
        presenter.onVote(true);

    }

    @OnClick(R.id.no_vote)
    public void onNoVote() {
        presenter.onVote(false);
    }

    public void setFailButtonEnable(boolean status) {
        noVote.setEnabled(status);
    }

    public void setMissionVote(@NonNull MissionVote missionVote) {
        adapter.setMissionVote(missionVote);
        adapter.notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }
}
