package com.leminiscate.transactions;

import dagger.Module;
import dagger.Provides;

@Module class TransactionsPresenterModule {
  private final TransactionsContract.View transactionsView;

  TransactionsPresenterModule(TransactionsContract.View view) {
    transactionsView = view;
  }

  @Provides TransactionsContract.View provideTransactionsContractView() {
    return transactionsView;
  }
}
