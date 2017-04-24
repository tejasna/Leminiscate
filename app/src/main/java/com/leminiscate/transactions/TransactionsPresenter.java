package com.leminiscate.transactions;

import android.app.Activity;
import com.leminiscate.data.Transaction;
import com.leminiscate.data.source.WalletDataSource;
import com.leminiscate.data.source.WalletRepository;
import com.leminiscate.spend.SpendActivity;
import com.leminiscate.utils.CurrencyConverterUtil;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

class TransactionsPresenter implements TransactionsContract.Presenter {

  private final WalletRepository repository;

  private final TransactionsContract.View transactionsView;

  private boolean firstLoad = true;

  @Inject TransactionsPresenter(WalletRepository tasksRepository,
      TransactionsContract.View transactionsView) {
    repository = tasksRepository;
    this.transactionsView = transactionsView;
  }

  @Inject void setupListeners() {
    transactionsView.setPresenter(this);
  }

  @Override public void start() {
    loadTransactions(false);
  }

  @Override public void stop() {
    repository.clearSubscriptions();
  }

  @Override public void loadTransactions(boolean forceUpdate) {
    loadTransactions(forceUpdate || firstLoad, true);
    firstLoad = false;
  }

  @Override public void result(int requestCode, int resultCode) {
    if (SpendActivity.REQUEST_NEW_TRANSACTION == requestCode && Activity.RESULT_OK == resultCode) {
      loadTransactions(true, true);
    }
  }

  @Override public void logout() {
    repository.logout();
  }

  private void loadTransactions(boolean forceUpdate, final boolean showLoadingUI) {
    if (showLoadingUI) {
      transactionsView.setLoadingIndicator(true);
    }
    if (forceUpdate) {
      repository.refreshTransactions();
    }

    repository.getTransactions(new WalletDataSource.LoadTransactionsCallback() {
      @Override public void onTransactionsLoaded(List<Transaction> transactions) {
        if (!transactionsView.isActive()) {
          return;
        }
        if (showLoadingUI) {
          transactionsView.setLoadingIndicator(false);
        }

        processTransactions(transactions);
      }

      @Override public void onDataNotAvailable() {
        if (!transactionsView.isActive()) {
          return;
        }
        transactionsView.showLoadingTransactionsError();
      }
    });
  }

  private void processTransactions(List<Transaction> transactions) {
    if (transactions.isEmpty()) {
      transactionsView.showNoTransactions();
    } else {
      Collections.reverse(transactions);
      for (Transaction transaction : transactions) {
        transaction.setAmountInNativeRate(String.valueOf(
            CurrencyConverterUtil.round(CurrencyConverterUtil.getAmountFromGBPTO(transaction), 2)));
      }
    }
    transactionsView.showTransactions(transactions.subList(0, 4));
  }
}

