package com.leminiscate.transactions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.leminiscate.R;

import static com.leminiscate.utils.PreConditions.checkNotNull;

public class TransactionsFragment extends Fragment implements TransactionsContract.View {

  private TransactionsContract.Presenter mPresenter;

  private Unbinder unbinder;

  public TransactionsFragment() {
  }

  public static TransactionsFragment newInstance() {
    return new TransactionsFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.transactions_frag, container, false);

    unbinder = ButterKnife.bind(this, rootView);

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

  @Override public void setPresenter(TransactionsContract.Presenter presenter) {
    mPresenter = checkNotNull(presenter);
  }
}
