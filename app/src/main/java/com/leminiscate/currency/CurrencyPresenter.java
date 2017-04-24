package com.leminiscate.currency;

import com.leminiscate.data.Currency;
import com.leminiscate.data.source.WalletDataSource;
import com.leminiscate.data.source.WalletRepository;
import java.util.List;
import javax.inject.Inject;

class CurrencyPresenter implements CurrencyContract.Presenter {

  private final WalletRepository mRepository;

  private final CurrencyContract.View mCurrencyView;

  private boolean mFirstLoad = true;

  @Inject CurrencyPresenter(WalletRepository tasksRepository, CurrencyContract.View currencyView) {
    mRepository = tasksRepository;
    mCurrencyView = currencyView;
  }

  @Inject void setupListeners() {
    mCurrencyView.setPresenter(this);
  }

  @Override public void loadCurrencies(boolean forceUpdate) {
    loadCurrencies(forceUpdate || mFirstLoad, false);
    mFirstLoad = false;
  }

  @Override public void savePreferredCurrency(Currency currency) {
    mRepository.savePreferredCurrency(currency);
  }

  @Override public void start() {
    loadCurrencies(false);
  }

  @Override public void stop() {
    mRepository.clearSubscriptions();
  }

  private void loadCurrencies(boolean forceUpdate, final boolean showLoadingUI) {

    if (!showLoadingUI) {
      if (forceUpdate) {
        mRepository.refreshTransactions();
      }

      mRepository.getCurrencies(new WalletDataSource.LoadCurrenciesCallback() {
        @Override public void onCurrencyLoaded(List<Currency> currencies) {
          if (!mCurrencyView.isActive()) {
            return;
          }
          processCurrencies(currencies);
        }

        @Override public void onDataNotAvailable() {
          if (!mCurrencyView.isActive()) {
            return;
          }
          mCurrencyView.showCurrenciesUnavailable();
        }
      });
    }
  }

  private void processCurrencies(List<Currency> currencies) {
    if (currencies == null) {
      mCurrencyView.showCurrenciesUnavailable();
    } else {
      mCurrencyView.showCurrencies(currencies);
    }
  }
}
