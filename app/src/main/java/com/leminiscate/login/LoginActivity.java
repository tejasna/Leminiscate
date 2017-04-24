package com.leminiscate.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.leminiscate.LeminiscateApplication;
import com.leminiscate.R;
import com.leminiscate.utils.ActivityUtils;
import javax.inject.Inject;

public class LoginActivity extends AppCompatActivity {

  @Inject LoginPresenter presenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_act);

    LoginFragment loginFragment =
        (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

    if (loginFragment == null) {
      loginFragment = LoginFragment.newInstance();
      ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), loginFragment,
          R.id.content_frame);
    }

    DaggerLoginComponent.builder()
        .walletRepositoryComponent(
            ((LeminiscateApplication) getApplication()).getWalletRepositoryComponent())
        .loginPresenterModule(new LoginPresenterModule(loginFragment))
        .build()
        .inject(this);
  }
}
