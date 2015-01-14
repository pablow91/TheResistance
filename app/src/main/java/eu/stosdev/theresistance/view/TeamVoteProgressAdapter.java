package eu.stosdev.theresistance.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.model.game.TeamVote;

public class TeamVoteProgressAdapter extends BaseAdapter {

    private final Context context;
    private final TeamVote teamVote;
    private final GameState gameState;

    private boolean showResults;

    @Inject
    public TeamVoteProgressAdapter(Context context, GameState gameState, TeamVote teamVote) {
        this.context = context;
        this.gameState = gameState;
        this.teamVote = teamVote;
    }

    @Override public int getCount() {
        return teamVote.getVoters().size();
    }

    @Override public Player getItem(int position) {
        return gameState.getPlayerByParticipant(teamVote.getVoters().get(position));
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View view, ViewGroup parent) {
        Container c;
        if (view == null) {
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.player_vote_item_list, parent, false);
            c = new Container();
            ButterKnife.inject(c, view);
            view.setTag(c);
        } else {
            c = (Container) view.getTag();
        }
        Player player = getItem(position);
        c.playerName.setText(player.getNick());
        boolean isChecked;
        if (showResults) {
            if (teamVote.getAllVotes().containsKey(player.getParticipantId())) {
                isChecked = teamVote.getAllVotes().get(player.getParticipantId());
            } else {
                isChecked = false;
            }
        } else {
            isChecked = teamVote.getAllVotes().containsKey(player.getParticipantId());
        }
        c.playerVoted.setChecked(isChecked);
        Picasso.with(context).load(Uri.parse(player.getPhotoUrl().or("https://lh3.googleusercontent.com/-WSXFQjD3OD0/AAAAAAAAAAI/AAAAAAAAAAA/AqqNntkfwBk/s120-c/photo.jpg"))).fit().into(c.playerPhoto);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            c.playerPhoto.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth() - view.getPaddingRight(), view.getHeight() - view.getPaddingBottom());
                }
            });
            c.playerPhoto.setClipToOutline(true);
        }
        return view;
    }

    public void showResults(boolean showResults) {
        this.showResults = showResults;
        notifyDataSetChanged();
    }

    public static class Container {
        @InjectView(R.id.player_photo) ImageView playerPhoto;
        @InjectView(R.id.player_name) TextView playerName;
        @InjectView(R.id.player_voted) CheckBox playerVoted;
    }
}
