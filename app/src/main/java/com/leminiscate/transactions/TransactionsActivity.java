package com.leminiscate.transactions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.leminiscate.LeminiscateApplication;
import com.leminiscate.R;
import com.leminiscate.utils.ActivityUtils;
import javax.inject.Inject;

public class TransactionsActivity extends AppCompatActivity {

  @Inject TransactionsPresenter mPresenter;

  @BindView(R.id.toolbar) Toolbar toolbar;

  private Unbinder unbinder;

  @SuppressWarnings("ConstantConditions") @Override
  protected void onCreate(Bundle savedInstanceState) {
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

  @Override protected void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
  }
}
