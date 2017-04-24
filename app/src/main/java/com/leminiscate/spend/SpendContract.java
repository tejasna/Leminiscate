package com.leminiscate.spend;

import com.leminiscate.BasePresenter;
import com.leminiscate.BaseView;
import com.leminiscate.data.Currency;

class SpendContract {

  interface View extends BaseView<SpendContract.Presenter> {

    void setLoadingIndicator(boolean active);

    void showInsufficientBalance();

    void showRequestBalanceError();

    void showAddedNewTransaction();

    void showAddedNewTransactionError();

    void showDialog(Currency preferredCurrency);

    void showPreferredCurrency(Currency preferredCurrency);

    boolean isActive();
  }

  interface Presenter extends BasePresenter {

    void showDialog(Currency preferredCurrency);

    void result(int requestCode, int resultCode);

    void spend(String description, String amount, String currency);
  }
}
