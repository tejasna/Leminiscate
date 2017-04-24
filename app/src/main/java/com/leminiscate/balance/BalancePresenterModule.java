package com.leminiscate.balance;

import dagger.Module;
import dagger.Provides;

@Module class BalancePresenterModule {
  private final BalanceContract.View view;

  BalancePresenterModule(BalanceContract.View view) {
    this.view = view;
  }

  @Provides BalanceContract.View provideLoginContractView() {
    return view;
  }
}
