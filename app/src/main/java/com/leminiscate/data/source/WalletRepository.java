package com.leminiscate.data.source;

import android.support.annotation.NonNull;
import com.leminiscate.data.Balance;
import com.leminiscate.data.Currency;
import com.leminiscate.data.Login;
import com.leminiscate.data.Transaction;
import io.realm.Realm;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

import static com.leminiscate.utils.PreConditions.checkNotNull;

@Singleton public class WalletRepository implements WalletDataSource {

  private final WalletDataSource remoteDataSource;

  private final WalletDataSource localDataSource;

  private Map<String, Transaction> cachedTransactions;

  private Map<String, Currency> cachedCurrencies;

  private Balance cachedBalance;

  private Currency cachedUserPrefCurrency;

  private boolean cacheIsDirty = false;

  @Inject WalletRepository(@Remote WalletDataSource tasksRemoteDataSource,
      @Local WalletDataSource tasksLocalDataSource) {
    remoteDataSource = tasksRemoteDataSource;
    localDataSource = tasksLocalDataSource;
  }

  @Override public void login(@NonNull LoginCallback callback) {

    localDataSource.checkLoginState(new LoginCallback() {
      @Override public void userExists() {
        callback.userExists();
      }

      @Override public void onLoginSuccess(Login login) {
        callback.onLoginSuccess(login);
      }

      @Override public void onLoginFailure() {
        remoteDataSource.login(callback);
      }
    });
  }

  @Override public void saveLoginState(@NonNull Login login) {
    localDataSource.saveLoginState(login);
  }

  @Override public void clearLoginState() {
    localDataSource.clearLoginState();
  }

  @Override public void getTransactions(@NonNull LoadTransactionsCallback callback) {
    if (cachedTransactions != null && !cacheIsDirty) {
      callback.onTransactionsLoaded(new ArrayList<>(cachedTransactions.values()));
      return;
    }

    if (cacheIsDirty) {
      getTransactionsFromRemoteDataSource(callback);
    } else {
      localDataSource.getTransactions(new LoadTransactionsCallback() {
        @Override public void onTransactionsLoaded(List<Transaction> transactions) {
          checkNotNull(transactions);
          refreshCache(transactions);
          callback.onTransactionsLoaded(new ArrayList<>(cachedTransactions.values()));
        }

        @Override public void onDataNotAvailable() {
          getTransactionsFromRemoteDataSource(callback);
        }
      });
    }
  }

  @Override public void saveTransactions(@NonNull List<Transaction> transactions) {
    checkNotNull(transactions);
    remoteDataSource.saveTransactions(transactions);
    localDataSource.saveTransactions(transactions);

    // Do in memory cache update to keep the app UI up to date
    if (cachedTransactions == null) {
      cachedTransactions = new LinkedHashMap<>();
    }
    for (Transaction transaction : transactions) {
      cachedTransactions.put(transaction.getId(), transaction);
    }
  }

  @Override
  public void newTransaction(@NonNull Transaction transaction, SaveTransactionCallback callback) {
    checkNotNull(transaction);
    remoteDataSource.newTransaction(transaction, callback);
  }

  @Override public void getBalance(@NonNull LoadBalanceCallback callback) {
    if (cachedBalance != null && !cacheIsDirty) {
      callback.onBalanceLoaded(cachedBalance, cachedUserPrefCurrency);
      return;
    }

    if (cacheIsDirty) {
      getBalanceFromRemoteDataSource(callback);
    } else {
      localDataSource.getBalance(new LoadBalanceCallback() {

        @Override public void onBalanceLoaded(Balance balance, Currency userPrefCurrency) {
          checkNotNull(balance);
          refreshBalanceCache(balance);
          callback.onBalanceLoaded(balance, userPrefCurrency);
        }

        @Override public void onDataNotAvailable() {
          getBalanceFromRemoteDataSource(callback);
        }
      });
    }
  }

  @Override public void saveBalance(@NonNull Balance balance) {
    checkNotNull(balance);
    remoteDataSource.saveBalance(balance);
    localDataSource.saveBalance(balance);

    if (cachedBalance == null) {
      cachedBalance = new Balance();
    }
    cachedBalance = balance;
  }

  @Override public void getCurrencies(@NonNull LoadCurrenciesCallback callback) {

    if (cachedCurrencies != null && !cacheIsDirty) {
      callback.onCurrencyLoaded(new ArrayList<>(cachedCurrencies.values()));
      return;
    }

    if (cacheIsDirty) {
      getCurrenciesFromRemoteDataSource(callback);
    } else {
      localDataSource.getCurrencies(new LoadCurrenciesCallback() {

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
    remoteDataSource.saveCurrencies(currencies);
    localDataSource.saveCurrencies(currencies);

    if (cachedCurrencies == null) {
      cachedCurrencies = new LinkedHashMap<>();
    }
    for (Currency currency : currencies) {
      cachedCurrencies.put(currency.getName(), currency);
    }
  }

  @Override public void getPreferredCurrency(@NonNull LoadCurrenciesCallback callback) {
    if (!cacheIsDirty && cachedUserPrefCurrency != null) {
      ArrayList<Currency> currencies = new ArrayList<>(1);
      currencies.add(cachedUserPrefCurrency);
      callback.onCurrencyLoaded(currencies);
    } else {
      localDataSource.getPreferredCurrency(new LoadCurrenciesCallback() {
        @Override public void onCurrencyLoaded(List<Currency> currencies) {
          checkNotNull(currencies);
          refreshCurrencyCache(currencies);
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
    refreshUserPrefCurrency(currency);
    remoteDataSource.savePreferredCurrency(currency);
    localDataSource.savePreferredCurrency(currency);
  }

  @Override
  public void isBalanceGreaterThan(@NonNull BalanceAvailabilityCallback callback, double amount) {
    remoteDataSource.getBalance(new LoadBalanceCallback() {

      @Override public void onBalanceLoaded(Balance balance, Currency userPrefCurrency) {
        refreshBalanceCache(balance);
        refreshUserPrefCurrency(userPrefCurrency);
        double d = Double.parseDouble(balance.getBalance());
        int balanceInInt = (int) d;
        if (balanceInInt >= amount) {
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
    remoteDataSource.getBalance(new LoadBalanceCallback() {

      @Override public void onBalanceLoaded(Balance balance, Currency userPrefCurrency) {
        refreshLocalDataSource(balance);
        refreshBalanceCache(balance);
        refreshUserPrefCurrency(userPrefCurrency);

        callback.onBalanceLoaded(cachedBalance, userPrefCurrency);
      }

      @Override public void onDataNotAvailable() {
        callback.onDataNotAvailable();
        localDataSource.getBalance(new LoadBalanceCallback() {

          @Override public void onBalanceLoaded(Balance balance, Currency userPrefCurrency) {
            checkNotNull(balance);
            refreshBalanceCache(balance);
            refreshUserPrefCurrency(userPrefCurrency);
            callback.onBalanceLoaded(balance, userPrefCurrency);
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
    remoteDataSource.getTransactions(new LoadTransactionsCallback() {
      @Override public void onTransactionsLoaded(List<Transaction> transactions) {
        checkNotNull(transactions);
        refreshCache(transactions);
        localDataSource.saveTransactions(transactions);
        callback.onTransactionsLoaded(new ArrayList<>(cachedTransactions.values()));
      }

      @Override public void onDataNotAvailable() {
        callback.onDataNotAvailable();
        localDataSource.getTransactions(new LoadTransactionsCallback() {
          @Override public void onTransactionsLoaded(List<Transaction> transactions) {
            checkNotNull(transactions);
            refreshCache(transactions);
            callback.onTransactionsLoaded(new ArrayList<>(cachedTransactions.values()));
          }

          @Override public void onDataNotAvailable() {
            callback.onDataNotAvailable();
          }
        });
      }
    });
  }

  private void getCurrenciesFromRemoteDataSource(@NonNull final LoadCurrenciesCallback callback) {
    remoteDataSource.getCurrencies(new LoadCurrenciesCallback() {

      @Override public void onCurrencyLoaded(List<Currency> currencies) {
        refreshCurrencyCache(currencies);
        callback.onCurrencyLoaded(new ArrayList<>(cachedCurrencies.values()));
      }

      @Override public void onDataNotAvailable() {
        callback.onDataNotAvailable();
        localDataSource.getTransactions(new LoadTransactionsCallback() {
          @Override public void onTransactionsLoaded(List<Transaction> transactions) {
            checkNotNull(transactions);
            refreshCache(transactions);
            callback.onCurrencyLoaded(new ArrayList<>(cachedCurrencies.values()));
          }

          @Override public void onDataNotAvailable() {
            callback.onDataNotAvailable();
          }
        });
      }
    });
  }

  @Override public void refreshTransactions() {
    cacheIsDirty = true;
  }

  @Override public void clearSubscriptions() {
    remoteDataSource.clearSubscriptions();
  }

  @Override public void logout() {
    Realm.getDefaultInstance().executeTransaction(realm -> realm.deleteAll());
    System.exit(0);
  }

  @Override public void deleteExistingBalance() {
    remoteDataSource.deleteExistingBalance();
    localDataSource.deleteExistingBalance();
  }

  @Override public void deleteAllTransactions() {
    remoteDataSource.deleteAllTransactions();
    localDataSource.deleteAllTransactions();

    if (cachedTransactions == null) {
      cachedTransactions = new LinkedHashMap<>();
    }
    cachedTransactions.clear();
  }

  @Override public void checkLoginState(@NonNull LoginCallback callback) {

  }

  //Helper methods to refresh transactions present in memory

  private void refreshCache(List<Transaction> transactions) {
    if (cachedTransactions == null) {
      cachedTransactions = new LinkedHashMap<>();
    }
    cachedTransactions.clear();
    for (Transaction transaction : transactions) {
      cachedTransactions.put(transaction.getId(), transaction);
    }
    cacheIsDirty = false;
  }

  private void refreshCurrencyCache(List<Currency> currencies) {
    if (cachedCurrencies == null) {
      cachedCurrencies = new LinkedHashMap<>();
    }
    cachedCurrencies.clear();
    for (Currency currency : currencies) {
      cachedCurrencies.put(currency.getName(), currency);
    }
    cacheIsDirty = false;
  }

  private void refreshUserPrefCurrency(Currency currency) {
    if (cachedUserPrefCurrency == null) {
      cachedUserPrefCurrency = new Currency();
    }
    cachedUserPrefCurrency.setResource(currency.getResource());
    cachedUserPrefCurrency.setName(currency.getName());
    cachedUserPrefCurrency.setUserPref(currency.getUserPref());
  }

  private void refreshBalanceCache(Balance balance) {
    if (cachedBalance == null) {
      cachedBalance = new Balance();
    }
    cachedBalance.setBalance(balance.getBalance());
    cachedBalance.setCurrency(balance.getCurrency());
  }

  private void refreshLocalDataSource(Balance balance) {
    localDataSource.saveBalance(balance);
  }
}