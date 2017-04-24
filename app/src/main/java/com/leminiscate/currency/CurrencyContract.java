package com.leminiscate.currency;

import com.leminiscate.BasePresenter;
import com.leminiscate.BaseView;
import com.leminiscate.data.Currency;
import java.util.List;

class CurrencyContract {
  interface View extends BaseView<CurrencyContract.Presenter> {

    void showCurrenciesUnavailable();

    void showCurrencies(List<Currency> currencies);

    boolean isActive();
  }

  interface Presenter extends BasePresenter {

    void loadCurrencies(boolean forceUpdate);

    void savePreferredCurrency(Currency currency);
  }
}
