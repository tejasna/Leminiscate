package com.leminiscate.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;
import com.leminiscate.data.Balance;
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
      realm.close();
    });
  }

  @Override public void saveBalance(@NonNull Balance balance) {
    checkNotNull(balance);
    Realm.getDefaultInstance().executeTransaction(realm -> {
      realm.copyToRealmOrUpdate(balance);
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

  /**
   * Note: {@link LoadTransactionsCallback# onDataNotAvailable()} is fired if the database doesn't
   * exist
   * or the table is empty.
   */

  /**
   * Note: {@link "GetTransactionsCallback# onDataNotAvailable()} is fired if the {@link } isn't
   * found.
   */

  @Override public void clearSubscriptions() {
    // Not required for the local data source because the {@link WalletRepository} handles
    // clearing the composite disposable
  }
}