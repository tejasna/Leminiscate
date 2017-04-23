package com.leminiscate.balance;

import com.leminiscate.data.Balance;
import com.leminiscate.data.source.WalletDataSource;
import com.leminiscate.data.source.WalletRepository;
import javax.inject.Inject;

class BalancePresenter implements BalanceContract.Presenter {

  private final WalletRepository mRepository;

  private final BalanceContract.View mBalanceView;

  private boolean mFirstLoad = true;

  @Inject BalancePresenter(WalletRepository tasksRepository, BalanceContract.View loginView) {
    mRepository = tasksRepository;
    mBalanceView = loginView;
  }

  @Inject void setupListeners() {
    mBalanceView.setPresenter(this);
  }

  @Override public void start() {
    loadBalance(false);
  }

  @Override public void stop() {
    mRepository.clearSubscriptions();
  }

  @Override public void loadBalance(boolean forceUpdate) {
    loadTransactions(forceUpdate || mFirstLoad, true);
    mFirstLoad = false;
  }

  private void loadTransactions(boolean forceUpdate, final boolean showLoadingUI) {
    if (showLoadingUI) {
      mBalanceView.setLoadingIndicator(true);
    }
    if (forceUpdate) {
      mRepository.refreshTransactions();
    }

    mRepository.getBalance(new WalletDataSource.LoadBalanceCallback() {

      @Override public void onBalanceLoaded(Balance balance) {
        if (!mBalanceView.isActive()) {
          return;
        }
        if (showLoadingUI) {
          mBalanceView.setLoadingIndicator(false);
        }

        processBalance(balance);
      }

      @Override public void onDataNotAvailable() {
        if (!mBalanceView.isActive()) {
          return;
        }
        mBalanceView.showLoadingBalanceError();
      }
    });
  }

  private void processBalance(Balance balance) {
    if (balance == null) {
      mBalanceView.showBalanceUnavailable();
    } else {
      mBalanceView.showBalance(balance);
    }
  }
}
