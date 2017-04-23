package com.leminiscate.balance;

import dagger.Module;
import dagger.Provides;

@Module class BalancePresenterModule {
  private final BalanceContract.View mView;

  BalancePresenterModule(BalanceContract.View view) {
    mView = view;
  }

  @Provides BalanceContract.View provideLoginContractView() {
    return mView;
  }
}
