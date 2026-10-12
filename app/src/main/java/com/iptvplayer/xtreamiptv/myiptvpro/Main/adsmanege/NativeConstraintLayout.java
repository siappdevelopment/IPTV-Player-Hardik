package com.iptvplayer.xtreamiptv.myiptvpro.Main.adsmanege;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.constraintlayout.widget.ConstraintLayout;

import kotlin.jvm.internal.DefaultConstructorMarker;

public final class NativeConstraintLayout extends ConstraintLayout {
    public NativeConstraintLayout(Context context) {
        this(context, null, 0, 6, null);
    }

    @Override
    public final boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == 4 && event.getAction() == 1) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public NativeConstraintLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0, 4, null);
    }

    public NativeConstraintLayout(Context context, AttributeSet attributeSet, int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i2 & 2) != 0 ? null : attributeSet, (i2 & 4) != 0 ? 0 : i);
    }

    public NativeConstraintLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setFocusableInTouchMode(true);
        requestFocus();
    }
}
