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

  @Override public void result(int requestCode, int resultCode) {
    if (SpendActivity.REQUEST_NEW_TRANSACTION == requestCode && Activity.RESULT_OK == resultCode) {
      loadTransactions(true, true);
    }
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
      Collections.reverse(transactions);
      for (Transaction transaction : transactions) {
        transaction.setAmountInNativeRate(String.valueOf(
            CurrencyConverterUtil.round(CurrencyConverterUtil.getAmountFromGBPTO(transaction), 2)));
      }
    }
    mTransactionsView.showTransactions(transactions.subList(0, 4));
  }
}

