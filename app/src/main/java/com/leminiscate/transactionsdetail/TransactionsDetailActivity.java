package com.leminiscate.transactionsdetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.leminiscate.LeminiscateApplication;
import com.leminiscate.R;
import com.leminiscate.spend.SpendActivity;
import com.leminiscate.utils.ActivityUtils;
import javax.inject.Inject;

public class TransactionsDetailActivity extends AppCompatActivity {

  @Inject TransactionsDetailPresenter mPresenter;

  @BindView(R.id.toolbar) Toolbar toolbar;

  private Unbinder unbinder;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.transactions_detail_act);

    unbinder = ButterKnife.bind(this);

    setSupportActionBar(toolbar);
    ActionBar ab = getSupportActionBar();
    //noinspection ConstantConditions
    ab.setDisplayHomeAsUpEnabled(true);
    ab.setDisplayShowHomeEnabled(true);

    TransactionsDetailFragment transactionsDetailFragment =
        (TransactionsDetailFragment) getSupportFragmentManager().findFragmentById(
            R.id.content_frame);

    if (transactionsDetailFragment == null) {
      transactionsDetailFragment = TransactionsDetailFragment.newInstance();
      ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), transactionsDetailFragment,
          R.id.content_frame);
    }

    DaggerTransactionsDetailComponent.builder()
        .walletRepositoryComponent(
            ((LeminiscateApplication) getApplication()).getWalletRepositoryComponent())
        .transactionsDetailPresenterModule(
            new TransactionsDetailPresenterModule(transactionsDetailFragment))
        .build()
        .inject(this);
  }

  @OnClick(R.id.fab_add_transaction) void addNewTransaction() {
    startActivityForResult(new Intent(this, SpendActivity.class),
        SpendActivity.REQUEST_NEW_TRANSACTION);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    mPresenter.result(requestCode, resultCode);
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
