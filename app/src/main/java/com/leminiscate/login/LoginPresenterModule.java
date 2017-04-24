package com.leminiscate.login;

import dagger.Module;
import dagger.Provides;

@Module class LoginPresenterModule {
  private final LoginContract.View loginView;

  LoginPresenterModule(LoginContract.View view) {
    loginView = view;
  }

  @Provides LoginContract.View provideLoginContractView() {
    return loginView;
  }
}
