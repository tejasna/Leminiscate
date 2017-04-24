package com.leminiscate.data.source;

import android.support.annotation.NonNull;
import com.leminiscate.data.Balance;
import com.leminiscate.data.Currency;
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

  private Map<String, Currency> mCachedCurrencies;

  private Balance mCachedBalance;

  private Currency mCachedPreferredCurrency;

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

  @Override
  public void newTransaction(@NonNull Transaction transaction, SaveTransactionCallback callback) {
    checkNotNull(transaction);
    mRemoteDataSource.newTransaction(transaction, callback);
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
          refreshBalanceCache(balance);
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

  @Override public void getCurrencies(@NonNull LoadCurrenciesCallback callback) {

    if (mCachedCurrencies != null && !mCacheIsDirty) {
      callback.onCurrencyLoaded(new ArrayList<>(mCachedCurrencies.values()));
      return;
    }

    if (mCacheIsDirty) {
      getCurrenciesFromRemoteDataSource(callback);
    } else {
      mLocalDataSource.getCurrencies(new LoadCurrenciesCallback() {

        @Override public void onCurrencyLoaded(List<Currency> currencies) {
          checkNotNull(currencies);
          refreshCurrencyCache(currencies);
          callback.onCurrencyLoaded(currencies);
        }

        @Override public void onDataNotAvailable() {
          getCurrenciesFromRemoteDataSource(callback);
        }
      });
    }
  }

  @Override public void saveCurrencies(@NonNull List<Currency> currencies) {
    checkNotNull(currencies);
    mRemoteDataSource.saveCurrencies(currencies);
    mLocalDataSource.saveCurrencies(currencies);

    if (mCachedCurrencies == null) {
      mCachedCurrencies = new LinkedHashMap<>();
    }
    for (Currency currency : currencies) {
      mCachedCurrencies.put(currency.getName(), currency);
    }
  }

  @Override public void getPreferredCurrency(@NonNull LoadCurrenciesCallback callback) {
    if (!mCacheIsDirty && mCachedPreferredCurrency != null) {
      ArrayList<Currency> currencies = new ArrayList<>(1);
      currencies.add(mCachedPreferredCurrency);
      callback.onCurrencyLoaded(currencies);
    } else {
      mLocalDataSource.getPreferredCurrency(new LoadCurrenciesCallback() {
        @Override public void onCurrencyLoaded(List<Currency> currencies) {
          checkNotNull(currencies);
          callback.onCurrencyLoaded(currencies);
        }

        @Override public void onDataNotAvailable() {
          callback.onDataNotAvailable();
        }
      });
    }
  }

  @Override public void savePreferredCurrency(@NonNull Currency currency) {
    checkNotNull(currency);
    mRemoteDataSource.savePreferredCurrency(currency);
    mLocalDataSource.savePreferredCurrency(currency);

    if (mCachedPreferredCurrency == null) {
      mCachedPreferredCurrency = new Currency();
    }
    mCachedPreferredCurrency = currency;
  }

  @Override
  public void isBalanceGreaterThan(@NonNull BalanceAvailabilityCallback callback, double amount) {
    mRemoteDataSource.getBalance(new LoadBalanceCallback() {
      @Override public void onBalanceLoaded(Balance balance) {
        refreshBalanceCache(balance);
        refreshLocalDataSource(balance);
        double d = Double.parseDouble(balance.getBalance());
        int balanceInInt = (int) d;
        if (balanceInInt > amount) {
          callback.onBalanceSufficient();
        } else {
          callback.onBalanceInsufficient();
        }
      }

      @Override public void onDataNotAvailable() {
        callback.onBalanceAvailabilityError();
      }
    });
  }

  private void getBalanceFromRemoteDataSource(@NonNull LoadBalanceCallback callback) {
    mRemoteDataSource.getBalance(new LoadBalanceCallback() {
      @Override public void onBalanceLoaded(Balance balance) {
        refreshBalanceCache(balance);
        refreshLocalDataSource(balance);
        callback.onBalanceLoaded(mCachedBalance);
      }

      @Override public void onDataNotAvailable() {
        callback.onDataNotAvailable();
        mLocalDataSource.getBalance(new LoadBalanceCallback() {
          @Override public void onBalanceLoaded(Balance balance) {
            checkNotNull(balance);
            refreshBalanceCache(balance);
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
        checkNotNull(transactions);
        refreshCache(transactions);
        mLocalDataSource.saveTransactions(transactions);
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

  private void getCurrenciesFromRemoteDataSource(@NonNull final LoadCurrenciesCallback callback) {
    mRemoteDataSource.getCurrencies(new LoadCurrenciesCallback() {

      @Override public void onCurrencyLoaded(List<Currency> currencies) {
        refreshCurrencyCache(currencies);
        callback.onCurrencyLoaded(new ArrayList<>(mCachedCurrencies.values()));
      }

      @Override public void onDataNotAvailable() {
        callback.onDataNotAvailable();
        mLocalDataSource.getTransactions(new LoadTransactionsCallback() {
          @Override public void onTransactionsLoaded(List<Transaction> transactions) {
            checkNotNull(transactions);
            refreshCache(transactions);
            callback.onCurrencyLoaded(new ArrayList<>(mCachedCurrencies.values()));
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

  private void refreshCurrencyCache(List<Currency> currencies) {
    if (mCachedCurrencies == null) {
      mCachedCurrencies = new LinkedHashMap<>();
    }
    mCachedCurrencies.clear();
    for (Currency currency : currencies) {
      mCachedCurrencies.put(currency.getName(), currency);
    }
    mCacheIsDirty = false;
  }

  private void refreshBalanceCache(Balance balance) {
    if (mCachedBalance == null) {
      mCachedBalance = new Balance();
    }
    mCachedBalance.setBalance(balance.getBalance());
    mCachedBalance.setCurrency(balance.getCurrency());
  }

  private void refreshLocalDataSource(Balance balance) {
    mLocalDataSource.deleteExistingBalance();
    mLocalDataSource.saveBalance(balance);
  }
}