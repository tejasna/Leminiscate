package com.leminiscate.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import com.leminiscate.data.Balance;
import com.leminiscate.data.Login;
import com.leminiscate.data.Transaction;
import com.leminiscate.data.source.NetModule;
import com.leminiscate.data.source.WalletDataSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import java.util.List;
import javax.inject.Singleton;
import timber.log.Timber;

import static com.leminiscate.utils.PreConditions.checkNotNull;
import static io.reactivex.Observable.empty;

@Singleton public class WalletRemoteDataSource implements WalletDataSource {

  private final WalletApi restApi;

  private CompositeDisposable compositeSubscription;

  private static final String authPrefix = "Bearer ";

  public WalletRemoteDataSource(@NonNull Context context) {
    checkNotNull(context);
    NetModule mNetModule = new NetModule();
    restApi = mNetModule.getRetrofit().create(WalletApi.class);
    compositeSubscription = new CompositeDisposable();
  }

  @Override public void login(final @NonNull LoginCallback callback) {
    compositeSubscription.add(restApi.login()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> {
          Timber.e(throwable.getMessage());
          callback.onLoginFailure();
        })
        .onErrorResumeNext(throwable -> {
          callback.onLoginFailure();
          return empty();
        })
        .subscribe(login -> {
          WalletRemoteDataSource.this.handleLoginResponse(login, callback);
        }));
  }

  @Override public void getTransactions(@NonNull LoadTransactionsCallback callback) {
    compositeSubscription.add(restApi.getTransactions(getAuthorizer())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> Timber.e(throwable.getMessage()))
        .onErrorResumeNext(throwable -> {
          callback.onDataNotAvailable();
          return empty();
        })
        .subscribe(callback::onTransactionsLoaded));
  }

  @Override public void saveTransactions(@NonNull List<Transaction> transactions) {

  }

  @Override public void getBalance(@NonNull LoadBalanceCallback callback) {
    compositeSubscription.add(restApi.getBalance(getAuthorizer())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> Timber.e(throwable.getMessage()))
        .onErrorResumeNext(throwable -> {
          callback.onDataNotAvailable();
          return empty();
        })
        .subscribe(callback::onBalanceLoaded));
  }

  @Override public void saveBalance(@NonNull Balance balance) {

  }

  private void handleLoginResponse(Login login, LoginCallback callback) {
    Timber.d(login.getToken());
    callback.onLoginSuccess(login);
  }

  @Override public void checkLoginState(@NonNull LoginCallback callback) {
    // Not required for the remote data source because the {@link TasksRepository} handles
  }

  @Override public void saveLoginState(@NonNull Login login) {
    // Not required for the remote data source because the {@link TasksRepository} handles
  }

  @Override public void clearLoginState() {
    // Not required for the remote data source because the {@link TasksRepository} handles
  }

  @Override public void clearSubscriptions() {
    compositeSubscription.clear();
  }

  /**
   * Note: {@link LoadTransactionsCallback# onDataNotAvailable()} is never fired. In a real remote
   * data
   * source implementation, this would be fired if the server can't be contacted or the server
   * returns an error.
   */

  /**
   * Note: {@link "GetTaskCallback#onDataNotAvailable()} is never fired. In a real remote data
   * source implementation, this would be fired if the server can't be contacted or the server
   * returns an error.
   */

  @Override public void refreshTransactions() {
    // Not required because the {@link TransactionsRepository} handles the logic of refreshing the
    // transactions from all the available data sources.
  }

  @Override public void deleteAllTransactions() {
    //TASKS_SERVICE_DATA.clear();
  }

  @Override public void deleteExistingBalance() {

  }

  private String getAuthorizer() {
    String bearer = Realm.getDefaultInstance().where(Login.class).findFirst().getToken();
    return authPrefix + bearer;
  }
}