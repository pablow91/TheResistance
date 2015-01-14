package eu.stosdev.theresistance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.model.game.Player;

public class FractionTypeView extends FrameLayout {

    private final TextView textView;

    public FractionTypeView(Context context) {
        this(context, null);
    }

    public FractionTypeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.ripple);
        textView = new TextView(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addView(textView, params);
        setFraction(null);
    }

    public void setFraction(@Nullable Player.Type playerType) {
        String text;
        if (playerType == null) {
            text = "U";
        } else {
            text = playerType.toString().substring(0, 1);
        }
        textView.setText(text);
    }
}
