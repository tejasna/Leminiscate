package com.leminiscate.spend;

import android.app.Activity;
import com.leminiscate.currency.CurrencyActivity;
import com.leminiscate.data.Currency;
import com.leminiscate.data.Transaction;
import com.leminiscate.data.source.WalletDataSource;
import com.leminiscate.data.source.WalletRepository;
import com.leminiscate.utils.CurrencyUtil;
import java.util.List;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import static com.leminiscate.utils.PreConditions.checkNotNull;

class SpendPresenter implements SpendContract.Presenter {

  private final WalletRepository repository;

  private final SpendContract.View spendView;

  @Inject SpendPresenter(WalletRepository tasksRepository, SpendContract.View loginView) {
    repository = tasksRepository;
    spendView = loginView;
  }

  @Inject void setupListeners() {
    spendView.setPresenter(this);
  }

  @Override public void start() {
    repository.getPreferredCurrency(new WalletDataSource.LoadCurrenciesCallback() {
      @Override public void onCurrencyLoaded(List<Currency> currencies) {
        if (!spendView.isActive()) {
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
    repository.clearSubscriptions();
  }

  @Override public void showDialog(Currency preferredCurrency) {
    spendView.showDialog(preferredCurrency);
  }

  @Override public void result(int requestCode, int resultCode) {
    if (CurrencyActivity.REQUEST_CURRENCY == requestCode && Activity.RESULT_OK == resultCode) {
      repository.getPreferredCurrency(new WalletDataSource.LoadCurrenciesCallback() {
        @Override public void onCurrencyLoaded(List<Currency> currencies) {
          if (!spendView.isActive()) {
            return;
          }
          checkNotNull(currencies);
          if (currencies.size() > 0) {
            spendView.showPreferredCurrency(currencies.get(0));
          } else {
            spendView.showPreferredCurrency(null);
          }
        }

        @Override public void onDataNotAvailable() {
          spendView.showPreferredCurrency(null);
        }
      });
    }
  }

  @Override public void spend(String description, String amountToSpend, String currency) {

    spendView.setLoadingIndicator(true);

    isBalanceGreaterThanAmountToSpend(description, amountToSpend, currency);
  }

  private void isBalanceGreaterThanAmountToSpend(String description, String amountToSpend,
      String currency) {

    double balanceInInt = CurrencyUtil.convertAmountToGBP(
        makeTransactionObject(description, amountToSpend, currency));

    repository.isBalanceGreaterThan(new WalletDataSource.BalanceAvailabilityCallback() {
      @Override public void onBalanceSufficient() {
        repository.newTransaction(makeTransactionObject(description, amountToSpend, currency),
            new WalletDataSource.SaveTransactionCallback() {
              @Override public void onTransactionSaved() {
                spendView.showAddedNewTransaction();
                repository.refreshTransactions();
              }

              @Override public void onTransactionSaveFailed() {
                spendView.showAddedNewTransactionError();
              }
            });
      }

      @Override public void onBalanceInsufficient() {
        spendView.showInsufficientBalance();
      }

      @Override public void onBalanceAvailabilityError() {
        spendView.showRequestBalanceError();
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
