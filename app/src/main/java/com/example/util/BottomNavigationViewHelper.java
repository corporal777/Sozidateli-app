package com.example.util;

import android.annotation.SuppressLint;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.lang.reflect.Field;


/**
 * Created by stanl on 04.10.2017.
 */

public class BottomNavigationViewHelper {

    private BottomNavigationView navigationView;

    public BottomNavigationViewHelper(BottomNavigationView navigationView) {
        this.navigationView = navigationView;
    }

    @SuppressLint("RestrictedApi")
    public boolean removeShiftMode() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
            item.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            // set once again checked value, so view will be updated
            item.setChecked(item.getItemData().isChecked());
        }
        menuView.buildMenuView();
        return true;
    }
}
