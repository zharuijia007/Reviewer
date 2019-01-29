package com.ruijiazha.reviewer.element;

import android.content.Context;
import android.widget.ImageButton;

public class StarImageButton extends android.support.v7.widget.AppCompatImageButton {

    int starid = 0;

    public StarImageButton(Context context) {
        super(context);
    }

    public int getStarid() {
        return starid;
    }

    public void setStarid(int starid) {
        this.starid = starid;
    }
}
