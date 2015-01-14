package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.RelativeLayout;

import butterknife.InjectView;
import eu.stosdev.theresistance.R;

import static butterknife.ButterKnife.inject;

public class ChooseTeamListItem extends RelativeLayout implements Checkable {

    private OnCheckCallback callback;
    @InjectView(R.id.checkbox) CheckBox checkBox;

    private int position;
    private boolean status;

    public ChooseTeamListItem(Context context) {
        super(context);
    }

    public ChooseTeamListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode()) {
            inject(this);
        }
    }

    public void initialize(int position, OnCheckCallback callback) {
        this.position = position;
        this.callback = callback;
    }

    @Override public void setChecked(boolean checked) {
        status = checked;
        checkBox.setChecked(checked);
        callback.onChange(position, status);
    }

    @Override public boolean isChecked() {
        return status;
    }

    @Override public void toggle() {
        setChecked(!status);
    }

    public interface OnCheckCallback {
        void onChange(int position, boolean status);
    }
}
