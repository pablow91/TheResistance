package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import eu.stosdev.theresistance.model.game.Player;
import eu.stosdev.theresistance.screen.ChooseTeamAwaitingScreen;
import mortar.Mortar;

public class ChooseTeamAwaitingView extends ListView {
    @Inject ChooseTeamAwaitingScreen.Presenter presenter;
    @Inject PlayerStatusAdapter adapter;

    public ChooseTeamAwaitingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Mortar.inject(context, this);
            setAdapter(adapter);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        if (!isInEditMode()) {
            presenter.takeView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }
    
    public void setPlayerList(List<Player> playerList){

    }

}
