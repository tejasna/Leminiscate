package com.leminiscate.balance;

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

public class BalanceActivity extends AppCompatActivity {

  @Inject BalancePresenter mPresenter;

  @BindView(R.id.toolbar) Toolbar toolbar;

  private Unbinder unbinder;

  private BalanceContract.Presenter presenter;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.balance_act);

    unbinder = ButterKnife.bind(this);

    setSupportActionBar(toolbar);
    ActionBar ab = getSupportActionBar();
    //noinspection ConstantConditions
    ab.setDisplayHomeAsUpEnabled(true);
    ab.setDisplayShowHomeEnabled(true);

    BalanceFragment balanceFragment =
        (BalanceFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

    if (balanceFragment == null) {
      balanceFragment = BalanceFragment.newInstance();
      ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), balanceFragment,
          R.id.content_frame);
    }

    DaggerBalanceComponent.builder()
        .walletRepositoryComponent(
            ((LeminiscateApplication) getApplication()).getWalletRepositoryComponent())
        .balancePresenterModule(new BalancePresenterModule(balanceFragment))
        .build()
        .inject(this);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }
}
