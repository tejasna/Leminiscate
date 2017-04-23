package com.leminiscate.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class SolomonTextViewRegular extends AppCompatTextView {
  public SolomonTextViewRegular(Context context) {
    super(context);
    init();
  }

  public SolomonTextViewRegular(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SolomonTextViewRegular(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    setTypeface(FontCache.regular);
  }
}
