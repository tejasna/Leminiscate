package com.leminiscate.transactionsdetail;

import dagger.Module;
import dagger.Provides;

@Module class TransactionsDetailPresenterModule {
  private final TransactionsDetailContract.View mView;

  TransactionsDetailPresenterModule(TransactionsDetailContract.View view) {
    mView = view;
  }

  @Provides TransactionsDetailContract.View provideTransactionsContractView() {
    return mView;
  }
}