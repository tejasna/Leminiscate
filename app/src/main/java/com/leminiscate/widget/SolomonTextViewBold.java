package com.leminiscate.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class SolomonTextViewBold extends AppCompatTextView {
  public SolomonTextViewBold(Context context) {
    super(context);
    init();
  }

  public SolomonTextViewBold(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SolomonTextViewBold(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    setTypeface(FontCache.bold);
  }
}
