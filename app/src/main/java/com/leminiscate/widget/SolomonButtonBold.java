package com.leminiscate.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

public class SolomonButtonBold extends AppCompatButton {
  public SolomonButtonBold(Context context) {
    super(context);
    init();
  }

  public SolomonButtonBold(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SolomonButtonBold(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    setTypeface(FontCache.bold);
  }
}
