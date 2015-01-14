package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import eu.stosdev.theresistance.screen.ChooseTeamScreen;
import mortar.Mortar;

public class ChooseTeamView extends ListView {

    @Inject ChooseTeamScreen.Presenter presenter;
    @Inject ChooseTeamListAdapter adapter;

    public ChooseTeamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Mortar.inject(context, this);
        }
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            ButterKnife.inject(this);
            setAdapter(adapter);
            setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    adapter.notifyDataSetChanged();
                }
            });
            presenter.takeView(this);
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }
}
