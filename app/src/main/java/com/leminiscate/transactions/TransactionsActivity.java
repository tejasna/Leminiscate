package com.leminiscate.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.leminiscate.LeminiscateApplication;
import com.leminiscate.R;
import com.leminiscate.spend.SpendActivity;
import com.leminiscate.utils.ActivityUtils;
import com.leminiscate.utils.CurrencyMapper;
import javax.inject.Inject;

public class TransactionsActivity extends AppCompatActivity {

  @Inject TransactionsPresenter presenter;

  @BindView(R.id.toolbar) Toolbar toolbar;

  @BindView(R.id.fab_add_transaction) FloatingActionButton fabAddTransaction;

  private Unbinder unbinder;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.transactions_act);

    unbinder = ButterKnife.bind(this);

    setSupportActionBar(toolbar);

    TransactionsFragment transactionsFragment =
        (TransactionsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);

    if (transactionsFragment == null) {
      transactionsFragment = TransactionsFragment.newInstance();
      ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), transactionsFragment,
          R.id.content_frame);
    }

    DaggerTransactionsComponent.builder()
        .walletRepositoryComponent(
            ((LeminiscateApplication) getApplication()).getWalletRepositoryComponent())
        .transactionsPresenterModule(new TransactionsPresenterModule(transactionsFragment))
        .build()
        .inject(this);

    CurrencyMapper.setDefaultCurrency();
  }

  @OnClick(R.id.fab_add_transaction) void addNewTransaction() {
    startActivityForResult(new Intent(this, SpendActivity.class),
        SpendActivity.REQUEST_NEW_TRANSACTION);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    presenter.result(requestCode, resultCode);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.transactions_menu, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_settings:
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
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
