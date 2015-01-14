package eu.stosdev.theresistance.model.card;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public abstract class PlotCard implements Serializable {

    @Getter @Setter private String owner;

    @NonNull public abstract Type getType();

    @StringRes public abstract int getName();

    @DrawableRes public abstract int getDrawable();

    public enum Type {
        ONE_TIME, INSTANT, PERMANENT
    }
}
