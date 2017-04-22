package com.leminiscate.login;

import android.support.annotation.NonNull;
import com.leminiscate.data.Login;
import com.leminiscate.data.source.WalletDataSource;
import com.leminiscate.data.source.WalletRepository;
import javax.inject.Inject;

class LoginPresenter implements LoginContract.Presenter {

  private final WalletRepository mRepository;

  private final LoginContract.View mLoginView;

  @Inject LoginPresenter(WalletRepository tasksRepository, LoginContract.View loginView) {
    mRepository = tasksRepository;
    mLoginView = loginView;
  }

  @Inject void setupListeners() {
    mLoginView.setPresenter(this);
  }

  @Override public void start() {

    login(new WalletDataSource.LoginCallback() {
      @Override public void userExists() {
        mLoginView.showActiveUser(true);
      }

      @Override public void onLoginSuccess(Login login) {
        mRepository.saveLoginState(login);
        mLoginView.showActiveUser(true);
      }

      @Override public void onLoginFailure() {
        mLoginView.showActiveUser(false);
        mLoginView.setRetryIndicator(true);
      }
    });
  }

  @Override public void stop() {
    mRepository.clearSubscriptions();
  }

  @Override public void login(@NonNull WalletDataSource.LoginCallback callback) {
    mLoginView.setRetryIndicator(false);
    mRepository.login(callback);
  }
}


