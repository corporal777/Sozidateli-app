package com.example.ui.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.example.R;

import androidx.appcompat.app.AlertDialog;


public class LoadingDialog extends AlertDialog {

    private int counter = 0;

    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    public void show() {
        this.show(true);
    }

    public void show(boolean cancelable) {
        counter++;
        setCancelable(cancelable);
        super.show();
    }

    @Override
    public void dismiss() {
        counter--;
        super.dismiss();
    }
}
