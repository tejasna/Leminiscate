package com.leminiscate.transactions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.leminiscate.LeminiscateApplication;
import com.leminiscate.R;
import com.leminiscate.login.DaggerLoginComponent;
import com.leminiscate.utils.ActivityUtils;

public class TransactionsActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.transactions_act);

    TransactionsFragment transactionsFragment =
        (TransactionsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

    if (transactionsFragment == null) {
      transactionsFragment = TransactionsFragment.newInstance();
      ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), transactionsFragment,
          R.id.content_frame);
    }

    DaggerLoginComponent.builder()
        .walletRepositoryComponent(
            ((LeminiscateApplication) getApplication()).getWalletRepositoryComponent())
        .transactionsPresenterModule(new TransactionsPresenterModule(transactionsFragment))
        .build()
        .inject(this);
  }
}
