package com.leminiscate.login;

import android.support.annotation.NonNull;
import com.leminiscate.BasePresenter;
import com.leminiscate.BaseView;
import com.leminiscate.data.source.WalletDataSource;

class LoginContract {

  interface View extends BaseView<Presenter> {

    void showActiveUser(boolean active);

    void setRetryIndicator(boolean active);
  }

  interface Presenter extends BasePresenter {

    void login(@NonNull WalletDataSource.LoginCallback callback);
  }
}
