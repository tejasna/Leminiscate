package com.leminiscate.currency;

import dagger.Module;
import dagger.Provides;

@Module class CurrencyPresenterModule {
  private final CurrencyContract.View mView;

  CurrencyPresenterModule(CurrencyContract.View view) {
    mView = view;
  }

  @Provides CurrencyContract.View provideLoginContractView() {
    return mView;
  }
}
