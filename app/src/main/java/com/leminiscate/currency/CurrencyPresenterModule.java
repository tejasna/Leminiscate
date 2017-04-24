package com.leminiscate.currency;

import dagger.Module;
import dagger.Provides;

@Module class CurrencyPresenterModule {
  private final CurrencyContract.View currencyView;

  CurrencyPresenterModule(CurrencyContract.View view) {
    currencyView = view;
  }

  @Provides CurrencyContract.View provideLoginContractView() {
    return currencyView;
  }
}
