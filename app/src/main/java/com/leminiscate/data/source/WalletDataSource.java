package com.leminiscate.data.source;

import android.support.annotation.NonNull;
import com.leminiscate.data.Balance;
import com.leminiscate.data.Currency;
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

    void onBalanceLoaded(Balance balance, Currency userPrefCurrency);

    void onDataNotAvailable();
  }

  interface LoadCurrenciesCallback {

    void onCurrencyLoaded(List<Currency> currencies);

    void onDataNotAvailable();
  }

  interface SaveTransactionCallback {

    void onTransactionSaved();

    void onTransactionSaveFailed();
  }

  interface BalanceAvailabilityCallback {

    void onBalanceSufficient();

    void onBalanceInsufficient();

    void onBalanceAvailabilityError();
  }

  void login(@NonNull LoginCallback callback);

  void checkLoginState(@NonNull LoginCallback callback);

  void saveLoginState(@NonNull Login login);

  void clearLoginState();

  void getTransactions(@NonNull LoadTransactionsCallback callback);

  void saveTransactions(@NonNull List<Transaction> transactions);

  void newTransaction(@NonNull Transaction transaction, SaveTransactionCallback callback);

  void getBalance(@NonNull LoadBalanceCallback callback);

  void saveBalance(@NonNull Balance balance);

  void getCurrencies(@NonNull LoadCurrenciesCallback callback);

  void saveCurrencies(@NonNull List<Currency> currencies);

  void getPreferredCurrency(@NonNull LoadCurrenciesCallback callback);

  void savePreferredCurrency(@NonNull Currency currency);

  void isBalanceGreaterThan(@NonNull BalanceAvailabilityCallback callback, double amount);

  void refreshTransactions();

  void deleteAllTransactions();

  void deleteExistingBalance();

  void clearSubscriptions();

  void logout();
}
