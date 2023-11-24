package com.example.ui.views.expandableTextView;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.RequiresApi;

public class TextViewSavedState extends View.BaseSavedState {
    String text;
    int isCollapsed;

    TextViewSavedState(Parcelable superState) {
        super(superState);
    }

    private TextViewSavedState(Parcel in) {
        super(in);
        text = in.readString();
        isCollapsed = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(text);
        out.writeInt(isCollapsed);
    }

    public static final Parcelable.Creator<TextViewSavedState> CREATOR
            = new Parcelable.Creator<TextViewSavedState>() {
        public TextViewSavedState createFromParcel(Parcel in) {
            return new TextViewSavedState(in);
        }

        public TextViewSavedState[] newArray(int size) {
            return new TextViewSavedState[size];
        }
    };
}
