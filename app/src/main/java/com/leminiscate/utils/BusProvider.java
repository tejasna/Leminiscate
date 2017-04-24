package com.leminiscate.utils;

import com.squareup.otto.Bus;

public class BusProvider {

  public static Bus mBus;

  public static Bus getInstance() {

    if (mBus == null) {
      mBus = new MainThreadBus();
    }

    return mBus;
  }
}

