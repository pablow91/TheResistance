package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.card.PlotCard;
import eu.stosdev.theresistance.model.game.GameState;

public class CardPickingAdapter extends BaseAdapter {
    private List<PlotCard> cardList;
    private final SparseBooleanArray used = new SparseBooleanArray();
    private final Context context;
    private final GameState gameState;

    @Inject
    public CardPickingAdapter(Context context, GameState gameState) {
        this.context = context;
        this.gameState = gameState;
    }

    public void setCardList(List<PlotCard> cardList) {
        this.cardList = cardList;
    }

    public void checkItem(PlotCard position) {
        used.put(cardList.indexOf(position), true);
        notifyDataSetChanged();
    }

    @Override public int getCount() {
        return cardList == null ? 0 : cardList.size();
    }

    @Override public PlotCard getItem(int position) {
        return cardList.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View view, ViewGroup parent) {
        Container c;
        if (view == null) {
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.card_list_item, parent, false);
            c = new Container();
            ButterKnife.inject(c, view);
            view.setTag(c);
        } else {
            c = (Container) view.getTag();
        }
        PlotCard item = getItem(position);
        c.card_name.setText(item.getName());
        if (item.getOwner() != null) {
            c.card_owner.setText(gameState.getPlayerByParticipant(item.getOwner()).getNick());
        }
        c.checkBox.setChecked(used.get(position, false));
        return view;
    }

    public final static class Container {
        @InjectView(R.id.checkbox) CheckBox checkBox;
        @InjectView(R.id.card_name) TextView card_name;
        @InjectView(R.id.card_owner) TextView card_owner;
    }

}
