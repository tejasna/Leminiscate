package com.leminiscate.balance;

import com.leminiscate.BasePresenter;
import com.leminiscate.BaseView;
import com.leminiscate.data.Balance;

class BalanceContract {

  interface View extends BaseView<BalanceContract.Presenter> {

    void setLoadingIndicator(boolean active);

    void showBalance(Balance balance);

    void showBalanceUnavailable();

    void showLoadingBalanceError();

    boolean isActive();
  }

  interface Presenter extends BasePresenter {

    void loadBalance(boolean forceUpdate);

    void result(int requestCode, int resultCode);
  }
}
