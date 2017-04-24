package com.leminiscate.spend;

import android.app.Activity;
import com.leminiscate.currency.CurrencyActivity;
import com.leminiscate.data.Currency;
import com.leminiscate.data.Transaction;
import com.leminiscate.data.source.WalletDataSource;
import com.leminiscate.data.source.WalletRepository;
import com.leminiscate.utils.CurrencyConverterUtil;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import static com.leminiscate.utils.PreConditions.checkNotNull;

class SpendPresenter implements SpendContract.Presenter {

  private final WalletRepository mRepository;

  private final SpendContract.View mSpendView;

  @Inject SpendPresenter(WalletRepository tasksRepository, SpendContract.View loginView) {
    mRepository = tasksRepository;
    mSpendView = loginView;
  }

  @Inject void setupListeners() {
    mSpendView.setPresenter(this);
  }

  @Override public void start() {
    mRepository.getPreferredCurrency(new WalletDataSource.LoadCurrenciesCallback() {
      @Override public void onCurrencyLoaded(List<Currency> currencies) {
        if (!mSpendView.isActive()) {
          return;
        }
        checkNotNull(currencies);
        if (currencies.size() > 0) {
          showDialog(currencies.get(0));
        } else {
          showDialog(null);
        }
      }

      @Override public void onDataNotAvailable() {
        showDialog(null);
      }
    });
  }

  @Override public void stop() {
    mRepository.clearSubscriptions();
  }

  @Override public void showDialog(Currency preferredCurrency) {
    mSpendView.showDialog(preferredCurrency);
  }

  @Override public void result(int requestCode, int resultCode) {
    if (CurrencyActivity.REQUEST_CURRENCY == requestCode && Activity.RESULT_OK == resultCode) {
      mRepository.getPreferredCurrency(new WalletDataSource.LoadCurrenciesCallback() {
        @Override public void onCurrencyLoaded(List<Currency> currencies) {
          if (!mSpendView.isActive()) {
            return;
          }
          checkNotNull(currencies);
          if (currencies.size() > 0) {
            mSpendView.showPreferredCurrency(currencies.get(0));
          } else {
            mSpendView.showPreferredCurrency(null);
          }
        }

        @Override public void onDataNotAvailable() {
          mSpendView.showPreferredCurrency(null);
        }
      });
    }
  }

  @Override public void spend(String description, String amountToSpend, String currency) {

    mSpendView.setLoadingIndicator(true);

    isBalanceGreaterThanAmountToSpend(description, amountToSpend, currency);
  }

  private void isBalanceGreaterThanAmountToSpend(String description, String amountToSpend,
      String currency) {

    double balanceInInt = CurrencyConverterUtil.convertAmountToGBP(
        makeTransactionObject(description, amountToSpend, currency));

    mRepository.isBalanceGreaterThan(new WalletDataSource.BalanceAvailabilityCallback() {
      @Override public void onBalanceSufficient() {
        mRepository.newTransaction(makeTransactionObject(description, amountToSpend, currency),
            new WalletDataSource.SaveTransactionCallback() {
              @Override public void onTransactionSaved() {
                mSpendView.showAddedNewTransaction();
                mRepository.refreshTransactions();
              }

              @Override public void onTransactionSaveFailed() {
                mSpendView.showAddedNewTransactionError();
              }
            });
      }

      @Override public void onBalanceInsufficient() {
        mSpendView.showInsufficientBalance();
      }

      @Override public void onBalanceAvailabilityError() {
        mSpendView.showRequestBalanceError();
      }
    }, balanceInInt);
  }

  private Transaction makeTransactionObject(String description, String amount, String currency) {
    Transaction transaction = new Transaction();
    transaction.setDescription(description);
    transaction.setAmount(amount);
    transaction.setCurrency(currency);
    transaction.setDate(new DateTime(DateTimeZone.UTC).toString());
    return transaction;
  }
}
