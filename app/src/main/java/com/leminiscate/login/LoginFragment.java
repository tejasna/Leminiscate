package com.leminiscate.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.leminiscate.transactions.TransactionsActivity;
import com.leminiscate.R;

import static com.leminiscate.utils.PreConditions.checkNotNull;

public class LoginFragment extends Fragment implements LoginContract.View {

  private LoginContract.Presenter mPresenter;

  private Unbinder unbinder;

  @BindView(R.id.img_reload) AppCompatImageView imgReload;

  public LoginFragment() {
  }

  public static LoginFragment newInstance() {
    return new LoginFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.login_frag, container, false);

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

  @Override public void setPresenter(LoginContract.Presenter presenter) {
    mPresenter = checkNotNull(presenter);
  }

  @Override public void showActiveUser(boolean active) {
    if (active) {
      startActivity(new Intent(getContext(), TransactionsActivity.class));
      getActivity().finish();
    } else {
      Toast.makeText(getContext(), getString(R.string.login_error), Toast.LENGTH_SHORT).show();
    }
  }

  @Override public void setRetryIndicator(boolean active) {
    int isVisible = active ? View.VISIBLE : View.GONE;
    imgReload.setVisibility(isVisible);
  }

  @OnClick(R.id.img_reload) public void reloadFrag() {
    mPresenter.start();
  }
}
