package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.card.PlotCard;
import eu.stosdev.theresistance.screen.CardPickingScreen;
import mortar.Mortar;

public class CardPickingView extends RelativeLayout {

    @Inject CardPickingScreen.Presenter presenter;
    @Inject CardPickingAdapter adapter;

    @InjectView(R.id.team_list) ListView listView;

    private List<PlotCard> cardList;

    public CardPickingView(Context context) {
        this(context, null);
    }

    public CardPickingView(Context context, AttributeSet attrs) {
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
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    presenter.onCardSelected(position);
                }
            });
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    public void notifyDataChangeSet() {
        adapter.notifyDataSetChanged();
    }

    public void setCardList(List<PlotCard> cardList) {
        this.cardList = cardList;
        adapter.setCardList(cardList);
        notifyDataChangeSet();
    }

    public void checkItem(PlotCard position) {
        adapter.checkItem(position);
    }
}
