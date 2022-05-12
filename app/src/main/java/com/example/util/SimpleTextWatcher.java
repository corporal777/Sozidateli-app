package com.example.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by stanl on 30.08.2017.
 */

public class SimpleTextWatcher implements TextWatcher {

    private BeforeTextChangeRunnable beforeTextChangeRunnable;
    private OnTextChangeRunnable onTextChangeRunnable;
    private AfterTextChangeRunnable afterTextChangeRunnable;

    public SimpleTextWatcher setBeforeTextChangeRunnable(BeforeTextChangeRunnable beforeTextChangeRunnable) {
        this.beforeTextChangeRunnable = beforeTextChangeRunnable;
        return this;
    }

    public SimpleTextWatcher setOnTextChangeRunnable(OnTextChangeRunnable onTextChangeRunnable) {
        this.onTextChangeRunnable = onTextChangeRunnable;
        return this;
    }

    public SimpleTextWatcher setAfterTextChangeRunnable(AfterTextChangeRunnable afterTextChangeRunnable) {
        this.afterTextChangeRunnable = afterTextChangeRunnable;
        return this;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (beforeTextChangeRunnable != null) beforeTextChangeRunnable.run(charSequence, i, i1, i2);
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (onTextChangeRunnable != null) onTextChangeRunnable.run(charSequence, i, i1, i2);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (afterTextChangeRunnable != null) afterTextChangeRunnable.run(editable);
    }

    public interface AfterTextChangeRunnable {
        void run(Editable editable);
    }

    public interface OnTextChangeRunnable {
        void run(CharSequence charSequence, int i, int i1, int i2);
    }

    public interface BeforeTextChangeRunnable {
        void run(CharSequence charSequence, int i, int i1, int i2);
    }
}
