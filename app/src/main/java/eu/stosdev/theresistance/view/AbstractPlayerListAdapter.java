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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.game.GameState;
import eu.stosdev.theresistance.model.game.Player;

public abstract class AbstractPlayerListAdapter extends BaseAdapter {
    private final Context context;
    protected final GameState gameState;

    public AbstractPlayerListAdapter(Context context, GameState gameState) {
        this.context = context;
        this.gameState = gameState;
    }

    @Override public abstract Player getItem(int position);

    @Override public final long getItemId(int position) {
        return position;
    }

    @Override public final View getView(int position, View view, ViewGroup parent) {
        Container c;
        if (view == null) {
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dashboard_list_item, parent, false);
            c = new Container();
            ButterKnife.inject(c, view);
            view.setTag(c);
        } else {
            c = (Container) view.getTag();
        }
        Player player = getItem(position);
        c.playerName.setText(player.getNick());
        c.playerType.setFraction(gameState.getPlayerTypeMap().containsKey(player) ? gameState.getPlayerTypeMap().get(player) : null);
        Picasso.with(context).load(Uri.parse(player.getPhotoUrl().or("https://lh3.googleusercontent.com/-WSXFQjD3OD0/AAAAAAAAAAI/AAAAAAAAAAA/AqqNntkfwBk/s120-c/photo.jpg"))).fit().into(c.playerPhoto);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            c.playerPhoto.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Override public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth() - view.getPaddingRight(), view.getHeight() - view.getPaddingBottom());
                }
            });
            c.playerPhoto.setClipToOutline(true);
        }
        return view;
    }

    public static class Container {
        @InjectView(R.id.player_photo) ImageView playerPhoto;
        @InjectView(R.id.player_name) TextView playerName;
        @InjectView(R.id.player_type) FractionTypeView playerType;
    }
}
