package com.iptvplayer.xtreamiptv.myiptvpro.Main.widget;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.view.MotionEvent;


public class WidgetView extends AppWidgetHostView {
    private long down;
    private OnLongClickListener longClick;
    private OnTouchListener onTouchListener;

    public WidgetView(Context context) {
        super(context);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        OnTouchListener onTouchListener = this.onTouchListener;
        if (onTouchListener != null) {
            onTouchListener.onTouch(this, motionEvent);
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.down = System.currentTimeMillis();
        } else if (actionMasked == 2) {
            if (System.currentTimeMillis() - this.down > 300) {
                this.longClick.onLongClick(this);
            }
        }
        return false;
    }

    @Override // android.view.View
    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.longClick = onLongClickListener;
    }

    @Override // android.view.View
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }
}
