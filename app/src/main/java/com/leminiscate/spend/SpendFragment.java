package com.leminiscate.spend;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.leminiscate.R;
import com.leminiscate.currency.CurrencyActivity;
import com.leminiscate.data.Currency;
import com.leminiscate.utils.CurrencyUtil;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

import static android.text.TextUtils.isEmpty;
import static com.leminiscate.utils.PreConditions.checkNotNull;

public class SpendFragment extends Fragment implements SpendContract.View {

  private Unbinder unbinder;

  private SpendContract.Presenter presenter;

  private Dialog newTransactionDialog;

  private AppCompatImageView preferredCurrencyImageView;

  private AppCompatTextView preferredCurrencyTextView;

  private AppCompatEditText descriptionView;

  private AppCompatEditText amountView;

  private AppCompatButton buttonView;

  private ProgressBar progress;

  private DisposableSubscriber<Boolean> disposableObserver = null;

  private Flowable<CharSequence> descriptionChangeObservable;

  private Flowable<CharSequence> amountChangeObservable;

  public SpendFragment() {
  }

  public static SpendFragment newInstance() {
    return new SpendFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.spend_frag, container, false);

    unbinder = ButterKnife.bind(this, rootView);

    presenter.start();

    return rootView;
  }

  @Override public void setLoadingIndicator(boolean active) {
    int visibility = active ? View.VISIBLE : View.GONE;
    progress.setVisibility(visibility);
    if (active) {
      buttonView.setVisibility(View.GONE);
    } else {
      buttonView.setVisibility(View.VISIBLE);
    }
  }

  @Override public void showInsufficientBalance() {
    Snackbar.make(amountView, getString(R.string.error_balance_insufficient), Snackbar.LENGTH_SHORT)
        .show();
    progress.setVisibility(View.GONE);
    buttonView.setVisibility(View.VISIBLE);
  }

  @Override public void showRequestBalanceError() {
    Snackbar.make(amountView, getString(R.string.error_request_balance), Snackbar.LENGTH_SHORT)
        .show();
    progress.setVisibility(View.GONE);
    buttonView.setVisibility(View.VISIBLE);
  }

  @Override public void showAddedNewTransaction() {
    Snackbar.make(amountView, getString(R.string.spend_successful), Snackbar.LENGTH_SHORT).show();
    if (newTransactionDialog.isShowing()) newTransactionDialog.cancel();
    getActivity().setResult(Activity.RESULT_OK);
    getActivity().finish();
  }

  @Override public void showAddedNewTransactionError() {
    Snackbar.make(amountView, getString(R.string.spend_error), Snackbar.LENGTH_SHORT).show();
    if (newTransactionDialog.isShowing()) newTransactionDialog.cancel();
    getActivity().setResult(Activity.RESULT_CANCELED);
    getActivity().finish();
  }

  private void combineLatestEvents() {
    disposableObserver = new DisposableSubscriber<Boolean>() {
      @Override public void onNext(Boolean formValid) {

        buttonView.setEnabled(formValid);
      }

      @Override public void onError(Throwable e) {
        Timber.e(e, "there was an error");
      }

      @Override public void onComplete() {
        Timber.d("completed");
      }
    };

    Flowable.combineLatest(descriptionChangeObservable, amountChangeObservable,
        (newDescription, newAmount) -> {
          boolean descValid = !isEmpty(newDescription);
          if (!descValid) {
            descriptionView.setError("Invalid Description!");
          }

          boolean amountValid = !isEmpty(newAmount) && newAmount.length() > 0;
          if (!amountValid) {
            amountView.setError("Invalid Amount!");
          }

          return descValid && amountValid;
        }).subscribe(disposableObserver);
  }

  @Override public void showDialog(Currency preferredCurrency) {
    if (newTransactionDialog == null || !newTransactionDialog.isShowing()) {

      LayoutInflater factory = LayoutInflater.from(getContext());

      View spendDialogView = factory.inflate(R.layout.new_transaction_dialog, null);

      buttonView = (AppCompatButton) spendDialogView.findViewById(R.id.btn_add);

      descriptionView = (AppCompatEditText) spendDialogView.findViewById(R.id.edt_description);

      amountView = (AppCompatEditText) spendDialogView.findViewById(R.id.edt_amount);

      progress = (ProgressBar) spendDialogView.findViewById(R.id.progress);

      newTransactionDialog = new Dialog(getContext(), R.style.DialogTheme);

      spendDialogView.findViewById(R.id.view_group_currency)
          .setOnClickListener(
              view -> startActivityForResult(new Intent(getContext(), CurrencyActivity.class),
                  CurrencyActivity.REQUEST_CURRENCY));

      spendDialogView.findViewById(R.id.btn_cancel)
          .setOnClickListener(view -> newTransactionDialog.cancel());

      buttonView.setOnClickListener(view -> {

        if (preferredCurrency != null) {

          presenter.spend(descriptionView.getText().toString().trim(),
              amountView.getText().toString().trim(),
              preferredCurrencyTextView.getText().toString().trim());
        } else {

          Snackbar.make(amountView, getString(R.string.error_invalid_currency),
              Snackbar.LENGTH_SHORT).show();
        }
      });

      preferredCurrencyTextView = ((AppCompatTextView) spendDialogView.findViewById(R.id.currency));

      preferredCurrencyImageView =
          ((AppCompatImageView) spendDialogView.findViewById(R.id.img_currency));

      if (preferredCurrency != null) {

        preferredCurrencyTextView.setText(preferredCurrency.getName());

        preferredCurrencyImageView.setBackgroundDrawable(
            ContextCompat.getDrawable(getContext(), CurrencyUtil.map(preferredCurrency.getName())));

        showDialog(spendDialogView);
      }
    }
  }

  private void showDialog(View view) {
    descriptionChangeObservable =
        RxTextView.textChanges(descriptionView).skip(1).toFlowable(BackpressureStrategy.LATEST);
    amountChangeObservable =
        RxTextView.textChanges(amountView).skip(1).toFlowable(BackpressureStrategy.LATEST);
    combineLatestEvents();

    newTransactionDialog.setContentView(view);
    newTransactionDialog.setOnCancelListener(dialogInterface -> getActivity().onBackPressed());

    //noinspection ConstantConditions
    newTransactionDialog.getWindow()
        .setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    newTransactionDialog.show();
  }

  @Override public void showPreferredCurrency(Currency preferredCurrency) {
    if (preferredCurrency != null && preferredCurrencyImageView != null) {
      preferredCurrencyTextView.setText(preferredCurrency.getName());
      preferredCurrencyImageView.setBackgroundDrawable(
          ContextCompat.getDrawable(getContext(), CurrencyUtil.map(preferredCurrency.getName())));
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    presenter.result(requestCode, resultCode);
  }

  @Override public boolean isActive() {
    return isAdded();
  }

  @Override public void setPresenter(SpendContract.Presenter presenter) {
    this.presenter = checkNotNull(presenter);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    unbinder.unbind();
    disposableObserver.dispose();
  }
}
