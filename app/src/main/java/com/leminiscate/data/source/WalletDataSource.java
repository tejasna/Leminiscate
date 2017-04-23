package com.leminiscate.data.source;

import android.support.annotation.NonNull;
import com.leminiscate.data.Balance;
import com.leminiscate.data.Login;
import com.leminiscate.data.Transaction;
import java.util.List;

public interface WalletDataSource {

  interface LoginCallback {

    void userExists();

    void onLoginSuccess(Login login);

    void onLoginFailure();
  }

  interface LoadTransactionsCallback {

    void onTransactionsLoaded(List<Transaction> transactions);

    void onDataNotAvailable();
  }

  interface LoadBalanceCallback {

    void onBalanceLoaded(Balance balance);

    void onDataNotAvailable();
  }

  void login(@NonNull LoginCallback callback);

  void checkLoginState(@NonNull LoginCallback callback);

  void saveLoginState(@NonNull Login login);

  void clearLoginState();

  void getTransactions(@NonNull LoadTransactionsCallback callback);

  void saveTransactions(@NonNull List<Transaction> transactions);

  void getBalance(@NonNull LoadBalanceCallback callback);

  void saveBalance(@NonNull Balance balance);

  void refreshTransactions();

  void deleteAllTransactions();

  void deleteExistingBalance();

  void clearSubscriptions();
}
