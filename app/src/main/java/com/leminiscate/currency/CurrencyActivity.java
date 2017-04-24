package com.leminiscate.currency;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.leminiscate.LeminiscateApplication;
import com.leminiscate.R;
import com.leminiscate.utils.ActivityUtils;
import javax.inject.Inject;

public class CurrencyActivity extends AppCompatActivity {

  public static final int REQUEST_CURRENCY = 1;

  @Inject CurrencyPresenter mPresenter;

  @BindView(R.id.toolbar) Toolbar toolbar;

  private Unbinder unbinder;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.currency_act);

    unbinder = ButterKnife.bind(this);

    setSupportActionBar(toolbar);
    ActionBar ab = getSupportActionBar();
    //noinspection ConstantConditions
    ab.setDisplayHomeAsUpEnabled(true);
    ab.setDisplayShowHomeEnabled(true);

    CurrencyFragment currencyFragment =
        (CurrencyFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

    if (currencyFragment == null) {
      currencyFragment = CurrencyFragment.newInstance();
      ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), currencyFragment,
          R.id.content_frame);
    }

    DaggerCurrencyComponent.builder()
        .walletRepositoryComponent(
            ((LeminiscateApplication) getApplication()).getWalletRepositoryComponent())
        .currencyPresenterModule(new CurrencyPresenterModule(currencyFragment))
        .build()
        .inject(this);
  }

  @Override public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }
}
