package com.leminiscate.transactions;

import dagger.Module;
import dagger.Provides;

@Module class TransactionsPresenterModule {
  private final TransactionsContract.View mView;

  TransactionsPresenterModule(TransactionsContract.View view) {
    mView = view;
  }

  @Provides TransactionsContract.View provideTransactionsContractView() {
    return mView;
  }
}
