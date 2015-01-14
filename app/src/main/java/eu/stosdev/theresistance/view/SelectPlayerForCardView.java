package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.screen.SelectPlayerForCardScreen;
import mortar.Mortar;

public class SelectPlayerForCardView extends ListView {

    @Inject SelectPlayerForCardScreen.Presenter presenter;
    @Inject SelectPlayerForCardAdapter adapter;

    public SelectPlayerForCardView(Context context) {
        this(context, null);
    }

    public SelectPlayerForCardView(Context context, AttributeSet attrs) {
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
            setAdapter(adapter);
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    presenter.onPlayerSelected(adapter.getItem(position));
                }
            });
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    public void setPlayers(List<Player> players) {
        adapter.setPlayers(players);
    }
}
