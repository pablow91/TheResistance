package eu.stosdev.theresistance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import eu.stosdev.theresistance.R;
import eu.stosdev.theresistance.utils.DpiConverter;
import mortar.Mortar;

import static com.google.common.base.Preconditions.checkArgument;

public class TurnProgressView extends LinearLayout {

    @Inject DpiConverter c;

    public final List<Element> votesResults = new ArrayList<>(5);

    public TurnProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        if (!isInEditMode()) {
            Mortar.inject(context, this);
            for (int i = 0; i <= 4; i++) {
                Element element = new Element(context);
                element.setElevation(c.dp(13));
                LinearLayout.LayoutParams params = new LayoutParams((int) c.dp(24), (int) c.dp(24));
                params.setMargins((int) c.dp(4), 0, (int) c.dp(4), 0);
                element.setLayoutParams(params);
                votesResults.add(element);
                addView(element);
            }
        }
    }

    public void setResult(int turn, boolean result) {
        checkArgument(turn >= 1 && turn <= 5, "Vote has to be between 1 and 5");
        votesResults.get(turn - 1).setWinner(result);
    }

    public void resetResults() {
        for (Element votesResult : votesResults) {
            votesResult.setImageResource(R.drawable.empty);
        }
    }

    public static class Element extends ImageView {

        public Element(Context context) {
            super(context);
            if (!isInEditMode()) {
                setImageResource(R.drawable.empty);
            }
        }

        public void setWinner(boolean result) {
            setImageResource(result ? R.drawable.green : R.drawable.red);
        }

    }
}
