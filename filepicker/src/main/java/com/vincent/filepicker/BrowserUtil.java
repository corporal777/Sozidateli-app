package com.vincent.filepicker;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;

public class BrowserUtil {
    public static void showBrowser(Context context, String url){
        try {
            CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
            intent.launchUrl(context, Uri.parse(url));
        } catch (Exception e){
            Toast.makeText(context, "Не удалось открыть страницу", Toast.LENGTH_LONG).show();
        }
    }
}
