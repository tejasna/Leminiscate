package com.leminiscate.data.source;

import android.support.annotation.NonNull;
import com.leminiscate.data.Balance;
import com.leminiscate.data.Login;
import com.leminiscate.data.Transaction;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

import static com.leminiscate.utils.PreConditions.checkNotNull;

@Singleton public class WalletRepository implements WalletDataSource {

  private final WalletDataSource mRemoteDataSource;

  private final WalletDataSource mLocalDataSource;

  private Map<String, Transaction> mCachedTransactions;

  private Balance mCachedBalance;

  private boolean mCacheIsDirty = false;

  @Inject WalletRepository(@Remote WalletDataSource tasksRemoteDataSource,
      @Local WalletDataSource tasksLocalDataSource) {
    mRemoteDataSource = tasksRemoteDataSource;
    mLocalDataSource = tasksLocalDataSource;
  }

  @Override public void login(@NonNull LoginCallback callback) {

    mLocalDataSource.checkLoginState(new LoginCallback() {
      @Override public void userExists() {
        callback.userExists();
      }

      @Override public void onLoginSuccess(Login login) {
        callback.onLoginSuccess(login);
      }

      @Override public void onLoginFailure() {
        mRemoteDataSource.login(callback);
      }
    });
  }

  @Override public void saveLoginState(@NonNull Login login) {
    mLocalDataSource.saveLoginState(login);
  }

  @Override public void clearLoginState() {
    mLocalDataSource.clearLoginState();
  }

  /**
   * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is
   * available first.
   * <p>
   * Note: {@link "LoadTasksCallback#onDataNotAvailable()} is fired if all data sources fail to
   * get the data.
   */
  @Override public void getTransactions(@NonNull LoadTransactionsCallback callback) {
    if (mCachedTransactions != null && !mCacheIsDirty) {
      callback.onTransactionsLoaded(new ArrayList<>(mCachedTransactions.values()));
      return;
    }

    if (mCacheIsDirty) {
      getTransactionsFromRemoteDataSource(callback);
    } else {
      mLocalDataSource.getTransactions(new LoadTransactionsCallback() {
        @Override public void onTransactionsLoaded(List<Transaction> transactions) {
          checkNotNull(transactions);
          refreshCache(transactions);
          callback.onTransactionsLoaded(new ArrayList<>(mCachedTransactions.values()));
        }

        @Override public void onDataNotAvailable() {
          getTransactionsFromRemoteDataSource(callback);
        }
      });
    }
  }

  @Override public void saveTransactions(@NonNull List<Transaction> transactions) {
    checkNotNull(transactions);
    mRemoteDataSource.saveTransactions(transactions);
    mLocalDataSource.saveTransactions(transactions);

    // Do in memory cache update to keep the app UI up to date
    if (mCachedTransactions == null) {
      mCachedTransactions = new LinkedHashMap<>();
    }
    for (Transaction transaction : transactions) {
      mCachedTransactions.put(transaction.getId(), transaction);
    }
  }

  @Override public void getBalance(@NonNull LoadBalanceCallback callback) {
    if (mCachedBalance != null && !mCacheIsDirty) {
      callback.onBalanceLoaded(mCachedBalance);
      return;
    }

    if (mCacheIsDirty) {
      getBalanceFromRemoteDataSource(callback);
    } else {
      mLocalDataSource.getBalance(new LoadBalanceCallback() {
        @Override public void onBalanceLoaded(Balance balance) {
          checkNotNull(balance);
          refreshCache(balance);
          callback.onBalanceLoaded(balance);
        }

        @Override public void onDataNotAvailable() {
          getBalanceFromRemoteDataSource(callback);
        }
      });
    }
  }

  @Override public void saveBalance(@NonNull Balance balance) {
    checkNotNull(balance);
    mRemoteDataSource.saveBalance(balance);
    mLocalDataSource.saveBalance(balance);

    if (mCachedBalance == null) {
      mCachedBalance = new Balance();
    }
    mCachedBalance = balance;
  }

  private void getBalanceFromRemoteDataSource(@NonNull LoadBalanceCallback callback) {
    mRemoteDataSource.getBalance(new LoadBalanceCallback() {
      @Override public void onBalanceLoaded(Balance balance) {
        refreshCache(balance);
        refreshLocalDataSource(balance);
        callback.onBalanceLoaded(mCachedBalance);
      }

      @Override public void onDataNotAvailable() {
        callback.onDataNotAvailable();
        mLocalDataSource.getBalance(new LoadBalanceCallback() {
          @Override public void onBalanceLoaded(Balance balance) {
            checkNotNull(balance);
            refreshCache(balance);
            callback.onBalanceLoaded(balance);
          }

          @Override public void onDataNotAvailable() {
            callback.onDataNotAvailable();
          }
        });
      }
    });
  }

  private void getTransactionsFromRemoteDataSource(
      @NonNull final LoadTransactionsCallback callback) {
    mRemoteDataSource.getTransactions(new LoadTransactionsCallback() {
      @Override public void onTransactionsLoaded(List<Transaction> transactions) {
        refreshCache(transactions);
        refreshLocalDataSource(transactions);
        callback.onTransactionsLoaded(new ArrayList<>(mCachedTransactions.values()));
      }

      @Override public void onDataNotAvailable() {
        callback.onDataNotAvailable();
        mLocalDataSource.getTransactions(new LoadTransactionsCallback() {
          @Override public void onTransactionsLoaded(List<Transaction> transactions) {
            checkNotNull(transactions);
            refreshCache(transactions);
            callback.onTransactionsLoaded(new ArrayList<>(mCachedTransactions.values()));
          }

          @Override public void onDataNotAvailable() {
            callback.onDataNotAvailable();
          }
        });
      }
    });
  }

  @Override public void refreshTransactions() {
    mCacheIsDirty = true;
  }

  @Override public void clearSubscriptions() {
    mRemoteDataSource.clearSubscriptions();
  }

  @Override public void deleteExistingBalance() {
    mRemoteDataSource.deleteExistingBalance();
    mLocalDataSource.deleteExistingBalance();
  }

  @Override public void deleteAllTransactions() {
    mRemoteDataSource.deleteAllTransactions();
    mLocalDataSource.deleteAllTransactions();

    if (mCachedTransactions == null) {
      mCachedTransactions = new LinkedHashMap<>();
    }
    mCachedTransactions.clear();
  }

  @Override public void checkLoginState(@NonNull LoginCallback callback) {

  }

  //Helper methods to refresh transactions present in memory

  private void refreshCache(List<Transaction> transactions) {
    if (mCachedTransactions == null) {
      mCachedTransactions = new LinkedHashMap<>();
    }
    mCachedTransactions.clear();
    for (Transaction transaction : transactions) {
      mCachedTransactions.put(transaction.getId(), transaction);
    }
    mCacheIsDirty = false;
  }

  private void refreshCache(Balance balance) {
    if (mCachedBalance == null) {
      mCachedBalance = new Balance();
    }
    mCachedBalance.setBalance(balance.getBalance());
    mCachedBalance.setCurrency(balance.getCurrency());
  }

  private void refreshLocalDataSource(List<Transaction> transactions) {
    mLocalDataSource.deleteAllTransactions();
    mLocalDataSource.saveTransactions(transactions);
  }

  private void refreshLocalDataSource(Balance balance) {
    mLocalDataSource.deleteExistingBalance();
    mLocalDataSource.saveBalance(balance);
  }
}