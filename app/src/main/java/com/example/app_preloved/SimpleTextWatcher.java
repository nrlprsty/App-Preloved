package com.example.app_preloved;

import android.text.Editable;
import android.text.TextWatcher;

public class SimpleTextWatcher implements TextWatcher {

    private final Runnable onChanged;

    public SimpleTextWatcher(Runnable onChanged) {
        this.onChanged = onChanged;
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        onChanged.run();
    }
}