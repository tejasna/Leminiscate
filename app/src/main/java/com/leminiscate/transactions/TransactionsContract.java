package com.leminiscate.transactions;

import com.leminiscate.BasePresenter;
import com.leminiscate.BaseView;
import com.leminiscate.data.Transaction;
import java.util.List;

class TransactionsContract {

  interface View extends BaseView<TransactionsContract.Presenter> {

    void setLoadingIndicator(boolean active);

    void showTransactions(List<Transaction> transactions);

    void showNoTransactions();

    void showLoadingTransactionsError();

    boolean isActive();
  }

  interface Presenter extends BasePresenter {

    void loadTransactions(boolean forceUpdate);

    void result(int requestCode, int resultCode);

    void logout();
  }
}

