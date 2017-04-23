package com.leminiscate.transactions;

import com.leminiscate.data.Transaction;
import com.leminiscate.data.source.WalletDataSource;
import com.leminiscate.data.source.WalletRepository;
import java.util.List;
import javax.inject.Inject;

class TransactionsPresenter implements TransactionsContract.Presenter {

  private final WalletRepository mRepository;

  private final TransactionsContract.View mTransactionsView;

  private boolean mFirstLoad = true;

  @Inject TransactionsPresenter(WalletRepository tasksRepository,
      TransactionsContract.View transactionsView) {
    mRepository = tasksRepository;
    mTransactionsView = transactionsView;
  }

  @Inject void setupListeners() {
    mTransactionsView.setPresenter(this);
  }

  @Override public void start() {
    loadTransactions(false);
  }

  @Override public void stop() {
    mRepository.clearSubscriptions();
  }

  @Override public void loadTransactions(boolean forceUpdate) {
    loadTransactions(forceUpdate || mFirstLoad, true);
    mFirstLoad = false;
  }

  private void loadTransactions(boolean forceUpdate, final boolean showLoadingUI) {
    if (showLoadingUI) {
      mTransactionsView.setLoadingIndicator(true);
    }
    if (forceUpdate) {
      mRepository.refreshTransactions();
    }

    mRepository.getTransactions(new WalletDataSource.LoadTransactionsCallback() {
      @Override public void onTransactionsLoaded(List<Transaction> transactions) {

        if (!mTransactionsView.isActive()) {
          return;
        }
        if (showLoadingUI) {
          mTransactionsView.setLoadingIndicator(false);
        }

        processTransactions(transactions);
      }

      @Override public void onDataNotAvailable() {
        if (!mTransactionsView.isActive()) {
          return;
        }
        mTransactionsView.showLoadingTransactionsError();
      }
    });
  }

  private void processTransactions(List<Transaction> transactions) {
    if (transactions.isEmpty()) {
      mTransactionsView.showNoTransactions();
    } else {
      mTransactionsView.showTransactions(transactions.subList(0, 4));
    }
  }
}
