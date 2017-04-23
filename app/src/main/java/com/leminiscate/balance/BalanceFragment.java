package com.leminiscate.balance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.leminiscate.R;
import com.leminiscate.data.Balance;

import static com.leminiscate.utils.PreConditions.checkNotNull;

public class BalanceFragment extends Fragment implements BalanceContract.View {

  @BindView(R.id.currency) TextView currencyView;

  @BindView(R.id.balance) TextView balanceView;

  @BindView(R.id.refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

  private Unbinder unbinder;

  private BalanceContract.Presenter mPresenter;

  public BalanceFragment() {
  }

  public static BalanceFragment newInstance() {
    return new BalanceFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.balance_frag, container, false);

    unbinder = ButterKnife.bind(this, rootView);

    swipeRefreshLayout.setOnRefreshListener(() -> mPresenter.loadBalance(true));

    return rootView;
  }

  @Override public void onResume() {
    super.onResume();
    mPresenter.start();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
    mPresenter.stop();
  }

  @Override public void setPresenter(BalanceContract.Presenter presenter) {
    mPresenter = checkNotNull(presenter);
  }

  @Override public void setLoadingIndicator(boolean active) {
    swipeRefreshLayout.setRefreshing(active);
  }

  @Override public void showBalance(Balance balance) {
    swipeRefreshLayout.setRefreshing(false);
    currencyView.setText(balance.getCurrency());
    balanceView.setText(balance.getBalance());
  }

  @Override public void showBalanceUnavailable() {
    swipeRefreshLayout.setRefreshing(false);
    Toast.makeText(getContext(), getString(R.string.balance_available), Toast.LENGTH_SHORT).show();
  }

  @Override public void showLoadingBalanceError() {
    swipeRefreshLayout.setRefreshing(false);
    Toast.makeText(getContext(), getString(R.string.balance_error), Toast.LENGTH_SHORT).show();
  }

  @Override public boolean isActive() {
    return isAdded();
  }
}
