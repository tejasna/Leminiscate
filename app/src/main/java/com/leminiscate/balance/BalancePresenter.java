package com.leminiscate.balance;

import android.app.Activity;
import com.leminiscate.data.Balance;
import com.leminiscate.data.Currency;
import com.leminiscate.data.source.WalletDataSource;
import com.leminiscate.data.source.WalletRepository;
import com.leminiscate.spend.SpendActivity;
import com.leminiscate.utils.CurrencyConverterUtil;
import java.util.List;
import javax.inject.Inject;

class BalancePresenter implements BalanceContract.Presenter {

  private final WalletRepository repository;

  private final BalanceContract.View balanceView;

  private boolean firstLoad = true;

  @Inject BalancePresenter(WalletRepository tasksRepository, BalanceContract.View loginView) {
    repository = tasksRepository;
    balanceView = loginView;
  }

  @Inject void setupListeners() {
    balanceView.setPresenter(this);
  }

  @Override public void start() {
    loadBalance(false);
  }

  @Override public void stop() {
    repository.clearSubscriptions();
  }

  @Override public void loadBalance(boolean forceUpdate) {
    loadBalance(forceUpdate || firstLoad, true);
    firstLoad = false;
  }

  @Override public void result(int requestCode, int resultCode) {
    if (SpendActivity.REQUEST_NEW_TRANSACTION == requestCode && Activity.RESULT_OK == resultCode) {
      loadBalance(true, true);
    }
  }

  private void loadBalance(boolean forceUpdate, final boolean showLoadingUI) {
    if (showLoadingUI) {
      balanceView.setLoadingIndicator(true);
    }
    if (forceUpdate) {
      repository.refreshTransactions();
    }

    repository.getBalance(new WalletDataSource.LoadBalanceCallback() {

      @Override public void onBalanceLoaded(Balance balance) {
        if (!balanceView.isActive()) {
          return;
        }
        if (showLoadingUI) {
          balanceView.setLoadingIndicator(false);
        }

        processBalance(balance);
      }

      @Override public void onDataNotAvailable() {
        if (!balanceView.isActive()) {
          return;
        }
        balanceView.showLoadingBalanceError();
      }
    });
  }

  private void processBalance(Balance balance) {
    if (balance == null) {
      balanceView.showBalanceUnavailable();
    } else {

      repository.getPreferredCurrency(new WalletDataSource.LoadCurrenciesCallback() {
        @Override public void onCurrencyLoaded(List<Currency> currencies) {
          if (currencies != null && currencies.size() > 0) {
            Balance preferredBalance =
                CurrencyConverterUtil.convertAmountToPreferredCurrency(currencies.get(0),
                    balance.getBalance());
            balanceView.showBalance(preferredBalance);
          }
        }

        @Override public void onDataNotAvailable() {
          balanceView.showBalanceUnavailable();
        }
      });
    }
  }
}
