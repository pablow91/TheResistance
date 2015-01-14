package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.mobeta.android.dslv.DragSortListView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.screen.ParticipantListScreen;
import mortar.Mortar;

public class ParticipantListView extends RelativeLayout implements View.OnClickListener {

    @Inject ParticipantListScreen.Presenter presenter;
    @Inject ParticipantListAdapter adapter;

    @InjectView(R.id.drag_sort_list) DragSortListView listView;
    @InjectView(R.id.plot_cards_checkbox) CheckBox checkBox;
    @InjectView(R.id.plot_cards_container) RelativeLayout checkBoxContainer;

    public ParticipantListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            Mortar.inject(context, this);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            ButterKnife.inject(this);
            listView.setAdapter(adapter);
            checkBoxContainer.setOnClickListener(this);
            presenter.takeView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    @Override public void onClick(View v) {
        checkBox.setChecked(!checkBox.isChecked());
    }

    public boolean usePlotCard() {
        return checkBox.isChecked();
    }
}
