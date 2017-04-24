package com.leminiscate.currency;

import com.leminiscate.data.Currency;
import com.leminiscate.data.source.WalletDataSource;
import com.leminiscate.data.source.WalletRepository;
import java.util.List;
import javax.inject.Inject;

class CurrencyPresenter implements CurrencyContract.Presenter {

  private final WalletRepository repository;

  private final CurrencyContract.View currencyView;

  private boolean mFirstLoad = true;

  @Inject CurrencyPresenter(WalletRepository tasksRepository, CurrencyContract.View currencyView) {
    repository = tasksRepository;
    this.currencyView = currencyView;
  }

  @Inject void setupListeners() {
    currencyView.setPresenter(this);
  }

  @Override public void loadCurrencies(boolean forceUpdate) {
    loadCurrencies(forceUpdate || mFirstLoad, false);
    mFirstLoad = false;
  }

  @Override public void savePreferredCurrency(Currency currency) {
    repository.savePreferredCurrency(currency);
  }

  @Override public void start() {
    loadCurrencies(false);
  }

  @Override public void stop() {
    repository.clearSubscriptions();
  }

  private void loadCurrencies(boolean forceUpdate, final boolean showLoadingUI) {

    if (!showLoadingUI) {
      if (forceUpdate) {
        repository.refreshTransactions();
      }

      repository.getCurrencies(new WalletDataSource.LoadCurrenciesCallback() {
        @Override public void onCurrencyLoaded(List<Currency> currencies) {
          if (!currencyView.isActive()) {
            return;
          }
          processCurrencies(currencies);
        }

        @Override public void onDataNotAvailable() {
          if (!currencyView.isActive()) {
            return;
          }
          currencyView.showCurrenciesUnavailable();
        }
      });
    }
  }

  private void processCurrencies(List<Currency> currencies) {
    if (currencies == null) {
      currencyView.showCurrenciesUnavailable();
    } else {
      currencyView.showCurrencies(currencies);
    }
  }
}
