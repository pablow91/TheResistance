package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import eu.stosdev.theresistance.model.game.Player;
import mortar.Mortar;
import mortar.ViewPresenter;

public abstract class SelectOnePlayerListView<T extends ViewPresenter<SelectOnePlayerListView<T>> & OnPlayerSelected> extends ListView {

    private final T presenter;
    @Inject SelectOnePlayerListAdapter adapter;

    public SelectOnePlayerListView(Context context, AttributeSet attrs, T presenter) {
        super(context, attrs);
        if (!isInEditMode()) {
            Mortar.inject(context, this);
        }
        this.presenter = presenter;
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

    public void setPlayerList(List<Player> playerList) {
        adapter.setPlayerList(playerList);
    }

    public void setResult(Boolean result) {
        Toast.makeText(getContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
    }
}
