package com.leminiscate.spend;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.leminiscate.LeminiscateApplication;
import com.leminiscate.R;
import com.leminiscate.utils.ActivityUtils;
import javax.inject.Inject;

public class SpendActivity extends AppCompatActivity {

  public static final int REQUEST_NEW_TRANSACTION = 2;

  @Inject SpendPresenter spendPresenter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.spend_act);

    SpendFragment spendFragment =
        (SpendFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

    if (spendFragment == null) {
      spendFragment = SpendFragment.newInstance();
      ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), spendFragment,
          R.id.content_frame);
    }

    DaggerSpendComponent.builder()
        .walletRepositoryComponent(
            ((LeminiscateApplication) getApplication()).getWalletRepositoryComponent())
        .spendPresenterModule(new SpendPresenterModule(spendFragment))
        .build()
        .inject(this);
  }
}
