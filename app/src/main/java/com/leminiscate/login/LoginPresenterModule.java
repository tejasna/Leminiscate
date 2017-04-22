package com.leminiscate.login;

import dagger.Module;
import dagger.Provides;

@Module class LoginPresenterModule {
  private final LoginContract.View mView;

  LoginPresenterModule(LoginContract.View view) {
    mView = view;
  }

  @Provides LoginContract.View provideLoginContractView() {
    return mView;
  }
}
