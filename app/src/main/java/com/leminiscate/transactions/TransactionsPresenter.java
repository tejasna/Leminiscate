package com.leminiscate.transactions;

import com.leminiscate.data.source.WalletRepository;
import javax.inject.Inject;

public class TransactionsPresenter implements TransactionsContract.Presenter {

  private final WalletRepository mRepository;

  private final TransactionsContract.View mTransactionsView;

  @Inject TransactionsPresenter(WalletRepository tasksRepository,
      TransactionsContract.View transactionsView) {
    mRepository = tasksRepository;
    mTransactionsView = transactionsView;
  }

  @Inject void setupListeners() {
    mTransactionsView.setPresenter(this);
  }

  @Override public void start() {
  }

  @Override public void stop() {
    mRepository.clearSubscriptions();
  }
}
