package com.leminiscate.spend;

import dagger.Module;
import dagger.Provides;

@Module class SpendPresenterModule {
  private final SpendContract.View spendView;

  SpendPresenterModule(SpendContract.View view) {
    spendView = view;
  }

  @Provides SpendContract.View provideLoginContractView() {
    return spendView;
  }
}