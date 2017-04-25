package com.leminiscate.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;
import com.leminiscate.data.Balance;
import com.leminiscate.data.Currency;
import com.leminiscate.data.Login;
import com.leminiscate.data.Transaction;
import com.leminiscate.data.source.WalletDataSource;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import static com.leminiscate.utils.PreConditions.checkNotNull;

@Singleton public class WalletLocalDataSource implements WalletDataSource {

  @Inject public WalletLocalDataSource(@NonNull Context context) {
    checkNotNull(context);
  }

  @Override public void checkLoginState(@NonNull LoginCallback callback) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      Login login = realm.where(Login.class).findFirst();
      if (login == null) {
        callback.onLoginFailure();
      } else {
        callback.userExists();
      }
    });
  }

  @Override public void login(@NonNull LoginCallback callback) {
    // Not required because the {@link TransactionsRepository} handles the logic of login locally
  }

  @Override public void saveLoginState(@NonNull Login login) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      realm.copyToRealmOrUpdate(login);
      realm.close();
    });
  }

  @Override public void clearLoginState() {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      Login login = realm.where(Login.class).findFirst();
      if (login != null) {
        login.deleteFromRealm();
      }
      realm.close();
    });
  }

  @Override public void getTransactions(@NonNull LoadTransactionsCallback callback) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      RealmResults<Transaction> transactions = realm.where(Transaction.class).findAll();
      if (transactions == null || transactions.size() < 1) {
        callback.onDataNotAvailable();
      } else {
        callback.onTransactionsLoaded(realm.copyFromRealm(transactions));
      }
      realm.close();
    });
  }

  @Override public void saveTransactions(@NonNull List<Transaction> transactions) {
    checkNotNull(transactions);
    Realm.getDefaultInstance().executeTransaction(realm -> {
      realm.copyToRealmOrUpdate(transactions);
      realm.close();
    });
  }

  @Override
  public void newTransaction(@NonNull Transaction transaction, SaveTransactionCallback callback) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      realm.copyToRealmOrUpdate(transaction);
      callback.onTransactionSaved();
      realm.close();
    });
  }

  @Override public void refreshTransactions() {
    // Not required because the {@link TransactionsRepository} handles the logic of refreshing the
    // transactions from all the available data sources.
  }

  @Override public void deleteAllTransactions() {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      RealmResults<Transaction> transactions = realm.where(Transaction.class).findAll();
      if (transactions != null) {
        transactions.deleteAllFromRealm();
      }
      realm.close();
    });
  }

  @Override public void getBalance(@NonNull LoadBalanceCallback callback) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      Balance balance = realm.where(Balance.class).findFirst();
      if (balance == null) {
        callback.onDataNotAvailable();
      } else {
        callback.onBalanceLoaded(realm.copyFromRealm(balance));
      }
     });
  }

  @Override public void saveBalance(@NonNull Balance balance) {
    checkNotNull(balance);
    Realm.getDefaultInstance().executeTransaction(realm -> {
      realm.copyToRealmOrUpdate(balance);
      realm.close();
    });
  }

  @Override public void getCurrencies(@NonNull LoadCurrenciesCallback callback) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      RealmResults<Currency> currencies = realm.where(Currency.class).findAll();
      if (currencies == null) {
        callback.onDataNotAvailable();
      } else {
        callback.onCurrencyLoaded(realm.copyFromRealm(currencies));
      }
      realm.close();
    });
  }

  @Override public void getPreferredCurrency(@NonNull LoadCurrenciesCallback callback) {
    if (!Realm.getDefaultInstance().isInTransaction()) {
      Realm.getDefaultInstance().executeTransaction(realm -> {
        RealmResults<Currency> currencies =
            realm.where(Currency.class).equalTo("preferred", true).findAll();
        if (currencies == null) {
          callback.onDataNotAvailable();
        } else {
          callback.onCurrencyLoaded(realm.copyFromRealm(currencies));
        }
        realm.close();
      });
    }
  }

  @Override public void savePreferredCurrency(@NonNull Currency newPreferredCurrency) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      Currency preferredCurrency =
          realm.where(Currency.class).equalTo("preferred", true).findFirst();
      if (preferredCurrency != null) {
        preferredCurrency.setPreferred(false);
        newPreferredCurrency.setPreferred(true);
        realm.copyToRealmOrUpdate(preferredCurrency);
        realm.copyToRealmOrUpdate(newPreferredCurrency);
      }
      realm.close();
    });
  }

  @Override
  public void isBalanceGreaterThan(@NonNull BalanceAvailabilityCallback callback, double amount) {
    // Not required for the local data source because the {@link WalletRepository} handles
  }

  @Override public void saveCurrencies(@NonNull List<Currency> currencies) {
    checkNotNull(currencies);
    Realm.getDefaultInstance().executeTransaction(realm -> {
      realm.copyToRealmOrUpdate(currencies);
      realm.close();
    });
  }

  @Override public void deleteExistingBalance() {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      Balance balance = realm.where(Balance.class).findFirst();
      if (balance != null) {
        balance.deleteFromRealm();
      }
      realm.close();
    });
  }

  @Override public void clearSubscriptions() {
    // Not required for the local data source because the {@link WalletRepository} handles
    // clearing the composite disposable
  }

  @Override public void logout() {

  }
}