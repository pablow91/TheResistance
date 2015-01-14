package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.screen.StrongLeaderScreen;
import mortar.Mortar;

public class StrongLeaderView extends RelativeLayout {

    @Inject StrongLeaderScreen.Presenter presenter;
    @Inject StrongLeaderListAdapter adapter;

    @InjectView(R.id.list_view) ListView listView;
    @InjectView(R.id.vote_bar) LinearLayout voteBar;

    public StrongLeaderView(Context context) {
        this(context, null);
    }

    public StrongLeaderView(Context context, AttributeSet attrs) {
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

    public void setPlayerList(List<Player> playerList) {
        adapter.setPlayerList(playerList);
    }

    public void activateButtons() {
        voteBar.setVisibility(VISIBLE);
        listView.setVisibility(GONE);
    }

    public void activateList() {
        voteBar.setVisibility(GONE);
        listView.setVisibility(VISIBLE);
    }

    public void disableViews() {
        voteBar.setVisibility(GONE);
        listView.setVisibility(GONE);
    }

    @OnClick(R.id.continue_button)
    public void onContinue() {
        presenter.onContinue();
    }

    @OnClick(R.id.become_button)
    public void onBecome() {
        presenter.onBecome();
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }
}
