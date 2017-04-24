package com.leminiscate.spend;

import dagger.Module;
import dagger.Provides;

@Module class SpendPresenterModule {
  private final SpendContract.View mView;

  SpendPresenterModule(SpendContract.View view) {
    mView = view;
  }

  @Provides SpendContract.View provideLoginContractView() {
    return mView;
  }
}