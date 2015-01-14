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

import com.google.android.gms.games.multiplayer.Participant;
import com.mobeta.android.dslv.DragSortListView;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import eu.stosdev.theresistance.R;

import static butterknife.ButterKnife.inject;

public class ParticipantListAdapter extends BaseAdapter implements DragSortListView.DropListener {

    private final Context context;
    private final List<Participant> participantList;

    @Inject
    public ParticipantListAdapter(Context context, List<Participant> participantList) {
        this.context = context;
        this.participantList = participantList;
    }

    @Override public int getCount() {
        return participantList.size();
    }

    @Override public Object getItem(int position) {
        return participantList.get(position);
    }

    @Override public long getItemId(int position) {
        return participantList.get(position).hashCode();
    }

    @Override public View getView(int position, View view, ViewGroup parent) {
        Container c;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.participant_list_item, parent, false);
            c = new Container();
            inject(c, view);
            view.setTag(c);
        } else {
            c = (Container) view.getTag();
        }
        Participant participant = participantList.get(position);
        c.tv.setText(participant.getDisplayName());
        String iconImageUri = participant.getHiResImageUrl();
        if (iconImageUri == null) {
            Picasso.with(context).load(Uri.parse("https://lh3.googleusercontent.com/-WSXFQjD3OD0/AAAAAAAAAAI/AAAAAAAAAAA/AqqNntkfwBk/s120-c/photo.jpg")).fit().into(c.ti);
        } else {
            Picasso.with(context).load(iconImageUri).fit().into(c.ti);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            c.ti.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth() - view.getPaddingRight(), view.getHeight() - view.getPaddingBottom());
                }
            });
            c.ti.setClipToOutline(true);
        }
        return view;
    }

    @Override
    public void drop(int from, int to) {
        Participant participant = participantList.remove(from);
        participantList.add(to, participant);
        notifyDataSetChanged();
    }

    public static class Container {
        @InjectView(R.id.person_name) TextView tv;
        @InjectView(R.id.person_photo) ImageView ti;
    }

}
