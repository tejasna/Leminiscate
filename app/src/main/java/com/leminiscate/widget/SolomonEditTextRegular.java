package com.leminiscate.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

public class SolomonEditTextRegular extends AppCompatEditText {
  public SolomonEditTextRegular(Context context) {
    super(context);
    init();
  }

  public SolomonEditTextRegular(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SolomonEditTextRegular(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    setTypeface(FontCache.regular);
  }
}
