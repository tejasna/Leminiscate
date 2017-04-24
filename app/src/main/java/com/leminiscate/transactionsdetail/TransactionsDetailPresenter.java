package com.leminiscate.transactionsdetail;

import android.app.Activity;
import com.leminiscate.data.Transaction;
import com.leminiscate.data.source.WalletDataSource;
import com.leminiscate.data.source.WalletRepository;
import com.leminiscate.spend.SpendActivity;
import com.leminiscate.utils.CurrencyConverterUtil;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class TransactionsDetailPresenter implements TransactionsDetailContract.Presenter {

  private final WalletRepository repository;

  private final TransactionsDetailContract.View transactionsView;

  private boolean mFirstLoad = true;

  @Inject TransactionsDetailPresenter(WalletRepository tasksRepository,
      TransactionsDetailContract.View transactionsView) {
    repository = tasksRepository;
    this.transactionsView = transactionsView;
  }

  @Inject void setupListeners() {
    transactionsView.setPresenter(this);
  }

  @Override public void loadTransactions(boolean forceUpdate) {
    loadTransactions(forceUpdate || mFirstLoad, true);
    mFirstLoad = false;
  }

  @Override public void result(int requestCode, int resultCode) {
    if (SpendActivity.REQUEST_NEW_TRANSACTION == requestCode && Activity.RESULT_OK == resultCode) {
      loadTransactions(true, true);
    }
  }

  @Override public void start() {
    loadTransactions(false);
  }

  @Override public void stop() {
    repository.clearSubscriptions();
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
      transactionsView.showTransactions(transactions);
    }
  }
}
