package com.leminiscate.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import java.util.Locale;

public class FontCache {

  static Typeface regular;
  static Typeface bold;

  public static void init(Context context) {
    AssetManager assetManager = context.getAssets();

    regular = Typeface.createFromAsset(assetManager,
        String.format(Locale.US, "font/%s", "SolomonSans-Regular.otf"));
    bold = Typeface.createFromAsset(assetManager,
        String.format(Locale.US, "font/%s", "SolomonSans-Bold.otf"));
  }
}
