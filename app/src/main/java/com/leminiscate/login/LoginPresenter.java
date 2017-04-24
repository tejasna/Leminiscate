package com.leminiscate.login;

import android.support.annotation.NonNull;
import com.leminiscate.data.Login;
import com.leminiscate.data.source.WalletDataSource;
import com.leminiscate.data.source.WalletRepository;
import javax.inject.Inject;

class LoginPresenter implements LoginContract.Presenter {

  private final WalletRepository repository;

  private final LoginContract.View loginView;

  @Inject LoginPresenter(WalletRepository tasksRepository, LoginContract.View loginView) {
    repository = tasksRepository;
    this.loginView = loginView;
  }

  @Inject void setupListeners() {
    loginView.setPresenter(this);
  }

  @Override public void start() {

    login(new WalletDataSource.LoginCallback() {
      @Override public void userExists() {
        loginView.showActiveUser(true);
      }

      @Override public void onLoginSuccess(Login login) {
        repository.saveLoginState(login);
        loginView.showActiveUser(true);
      }

      @Override public void onLoginFailure() {
        loginView.showActiveUser(false);
        loginView.setRetryIndicator(true);
      }
    });
  }

  @Override public void stop() {
    repository.clearSubscriptions();
  }

  @Override public void login(@NonNull WalletDataSource.LoginCallback callback) {
    loginView.setRetryIndicator(false);
    repository.login(callback);
  }
}


