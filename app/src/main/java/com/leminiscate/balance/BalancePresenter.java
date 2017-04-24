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
    loadBalance(forceUpdate || mFirstLoad, true);
    mFirstLoad = false;
  }

  @Override public void result(int requestCode, int resultCode) {
    if (SpendActivity.REQUEST_NEW_TRANSACTION == requestCode && Activity.RESULT_OK == resultCode) {
      loadBalance(true, true);
    }
  }

  private void loadBalance(boolean forceUpdate, final boolean showLoadingUI) {
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

      mRepository.getPreferredCurrency(new WalletDataSource.LoadCurrenciesCallback() {
        @Override public void onCurrencyLoaded(List<Currency> currencies) {
          if (currencies != null && currencies.size() > 0) {
            Balance preferredBalance =
                CurrencyConverterUtil.convertAmountToPreferredCurrency(currencies.get(0),
                    balance.getBalance());
            mBalanceView.showBalance(preferredBalance);
          }
        }

        @Override public void onDataNotAvailable() {
          mBalanceView.showBalanceUnavailable();
        }
      });
    }
  }
}
