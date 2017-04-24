package com.leminiscate.transactionsdetail;

import dagger.Module;
import dagger.Provides;

@Module class TransactionsDetailPresenterModule {
  private final TransactionsDetailContract.View transactionsView;

  TransactionsDetailPresenterModule(TransactionsDetailContract.View view) {
    transactionsView = view;
  }

  @Provides TransactionsDetailContract.View provideTransactionsContractView() {
    return transactionsView;
  }
}