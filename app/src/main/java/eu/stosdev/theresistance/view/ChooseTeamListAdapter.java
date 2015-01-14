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

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;

public class ChooseTeamListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Player> teamPlayerList;
    private final GameState gameState;
    private final ChooseTeamListItem.OnCheckCallback callback;

    @Inject
    public ChooseTeamListAdapter(Context context, GameState gameState, ChooseTeamListItem.OnCheckCallback callback) {
        this.context = context;
        this.teamPlayerList = gameState.getPlayerList();
        this.gameState = gameState;
        this.callback = callback;
    }

    @Override public int getCount() {
        return teamPlayerList.size();
    }

    @Override public Object getItem(int position) {
        return teamPlayerList.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View preView, ViewGroup parent) {
        Container c;
        ChooseTeamListItem view;
        if (preView == null) {
            view = (ChooseTeamListItem) ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.player_list_item, parent, false);
            c = new Container();
            ButterKnife.inject(c, view);
            view.setTag(c);
        } else {
            view = (ChooseTeamListItem) preView;
            c = (Container) view.getTag();
        }
        view.initialize(position, callback);
        Player player = (Player) getItem(position);
        c.playerName.setText(player.getNick());
        c.playerType.setFraction(gameState.getPlayerTypeMap().containsKey(player) ? gameState.getPlayerTypeMap().get(player) : null);
        Picasso.with(context).load(Uri.parse(player.getPhotoUrl().or("https://lh3.googleusercontent.com/-WSXFQjD3OD0/AAAAAAAAAAI/AAAAAAAAAAA/AqqNntkfwBk/s120-c/photo.jpg"))).into(c.playerPhoto);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            c.playerPhoto.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(21)
                @Override public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth() - view.getPaddingRight(), view.getHeight() - view.getPaddingBottom());
                }
            });
            c.playerPhoto.setClipToOutline(true);
        }
        c.checkBox.setFocusable(false);
        c.checkBox.setClickable(false);
        return view;
    }

    public static class Container {
        @InjectView(R.id.player_photo) ImageView playerPhoto;
        @InjectView(R.id.player_name) TextView playerName;
        @InjectView(R.id.player_type) FractionTypeView playerType;
        @InjectView(R.id.checkbox) CheckBox checkBox;
    }

}

