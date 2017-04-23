package com.leminiscate.transactionsdetail;

import com.leminiscate.BasePresenter;
import com.leminiscate.BaseView;
import com.leminiscate.data.Transaction;
import java.util.List;

public class TransactionsDetailContract {

  interface View extends BaseView<TransactionsDetailContract.Presenter> {

    void setLoadingIndicator(boolean active);

    void showTransactions(List<Transaction> transactions);

    void showNoTransactions();

    void showLoadingTransactionsError();

    boolean isActive();
  }

  interface Presenter extends BasePresenter {

    void loadTransactions(boolean forceUpdate);
  }
}
