package com.leminiscate.balance;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.leminiscate.R;
import com.leminiscate.currency.CurrencyActivity;
import com.leminiscate.data.Balance;

import static com.leminiscate.utils.PreConditions.checkNotNull;

public class BalanceFragment extends Fragment implements BalanceContract.View {

  @BindView(R.id.currency) TextView currencyView;

  @BindView(R.id.balance) TextView balanceView;

  @BindView(R.id.img_currency) ImageView imgCurrencyView;

  @BindView(R.id.refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

  private Unbinder unbinder;

  private BalanceContract.Presenter presenter;

  public BalanceFragment() {
  }

  public static BalanceFragment newInstance() {
    return new BalanceFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.balance_frag, container, false);

    unbinder = ButterKnife.bind(this, rootView);

    swipeRefreshLayout.setOnRefreshListener(() -> presenter.loadBalance(true));

    return rootView;
  }

  @OnClick(R.id.currency_prefs) public void navigateToCurrencyScreen() {
    startActivityForResult(new Intent(getContext(), CurrencyActivity.class),
        CurrencyActivity.REQUEST_CURRENCY);
  }

  @Override public void onResume() {
    super.onResume();
    presenter.start();
  }

  @Override public void setPresenter(BalanceContract.Presenter presenter) {
    this.presenter = checkNotNull(presenter);
  }

  @Override public void setLoadingIndicator(boolean active) {
    swipeRefreshLayout.setRefreshing(active);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    presenter.result(requestCode, resultCode);
  }

  @Override public void showBalance(Balance balance) {
    swipeRefreshLayout.setRefreshing(false);
    currencyView.setText(balance.getCurrency());
    balanceView.setText(balance.getBalance());
    imgCurrencyView.setImageDrawable(
        ContextCompat.getDrawable(getContext(), CurrencyMapper.map(balance.getCurrency())));
  }

  @Override public void showBalanceUnavailable() {
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override public void showLoadingBalanceError() {
    swipeRefreshLayout.setRefreshing(false);
    Snackbar.make(swipeRefreshLayout, getString(R.string.balance_error), Snackbar.LENGTH_SHORT)
        .show();
  }

  @Override public boolean isActive() {
    return isAdded();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
    presenter.stop();
  }
}
