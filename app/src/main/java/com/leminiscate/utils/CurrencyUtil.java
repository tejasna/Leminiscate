package com.leminiscate.utils;

import com.leminiscate.R;
import com.leminiscate.data.Balance;
import com.leminiscate.data.Currency;
import com.leminiscate.data.Transaction;
import io.realm.Realm;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyUtil {

  /**
   * A temporary static currency rate class
   * Holds the currency rates for a sample space of 5 countries
   * The currency conversion should ideally take place in a secure server @link{fixer.io}
   * Using this for temporary testing
   */
  private static class ConversionTable {
    static final double USD = 1.28;
    static final double JPY = 141.21;
    static final double CHF = 1.27;
    static final double CAD = 1.72;
    static final double GBP = 1;
  }

  /**
   * converts the amount of a transaction from GBP to transaction native form
   * uses the private field in {@link Transaction#amountInNativeRate} to perform the
   * conversion
   * Doing this since the current server supports transactions only in GBP
   * Should be fixed in production!
   *
   * @param transaction {@link Transaction}
   * @return the amount of the transaction in native rate
   */
  public static double convertFromGBP(Transaction transaction) {
    double d = Double.parseDouble(transaction.getAmount());

    switch (transaction.getCurrency()) {
      case "USD":
        return d * ConversionTable.USD;
      case "JPY":
        return d * ConversionTable.JPY;
      case "CHF":
        return d * ConversionTable.CHF;
      case "CAD":
        return d * ConversionTable.CAD;
      default:
        return d * ConversionTable.GBP;
    }
  }

  /**
   * converts the amount a transaction from native form to GBP
   *
   * @param transaction transaction {@link Transaction}
   * @return the amount of the transaction in GBP rate
   */
  public static double convertAmountToGBP(Transaction transaction) {
    try {
      double d = Double.parseDouble(transaction.getAmount());

      switch (transaction.getCurrency()) {
        case "USD":
          return d / ConversionTable.USD;
        case "JPY":
          return d / ConversionTable.JPY;
        case "CHF":
          return d / ConversionTable.CHF;
        case "CAD":
          return d / ConversionTable.CAD;
        default:
          return d / ConversionTable.GBP;
      }
    } catch (NumberFormatException e) {
      return 1 / ConversionTable.GBP;
    }
  }

  /**
   * A helper method to convert the amount of a transaction to a specified currency for display
   * based on a user pref.
   * Uses the static conversion rates stored above {@link CurrencyUtil.ConversionTable}
   *
   * @param currency the currency to be converted to
   * @param amount the amount of the transaction
   */
  public static Balance convertAmountToCurrency(Currency currency, String amount) {
    double d = Double.parseDouble(amount);
    Balance balance = new Balance();
    switch (currency.getName()) {
      case "USD":
        balance.setCurrency("USD");
        balance.setBalance(String.valueOf(round(d * ConversionTable.USD, 2)));
        return balance;
      case "JPY":
        balance.setCurrency("JPY");
        balance.setBalance(String.valueOf(round(d * ConversionTable.JPY, 2)));
        return balance;
      case "CHF":
        balance.setCurrency("CHF");
        balance.setBalance(String.valueOf(round(d * ConversionTable.CHF, 2)));
        return balance;
      case "CAD":
        balance.setCurrency("CAD");
        balance.setBalance(String.valueOf(round(d * ConversionTable.CAD, 2)));
        return balance;
      default:
        balance.setCurrency("GBP");
        balance.setBalance(String.valueOf(round(d * ConversionTable.GBP, 2)));
        return balance;
    }
  }

  public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  /**
   * maps the Currency code to its drawable id to display currency image
   * A better solution is needed for production
   */
  public static int map(String s) {
    int id;
    switch (s) {
      case "CAD":
        id = R.drawable.cad;
        break;
      case "GBP":
        id = R.drawable.gbp;
        break;
      case "JPY":
        id = R.drawable.jpy;
        break;
      case "CHF":
        id = R.drawable.chf;
        break;
      case "USD":
        id = R.drawable.usd;
        break;
      default:
        id = R.drawable.usd;
    }
    return id;
  }

  /**
   * Sets the default currency to be used throughout the app to GBP
   */
  public static void setDefaultCurrencyIfNull() {

    Realm.getDefaultInstance().executeTransaction(realm -> {

      if (realm.where(Currency.class).equalTo("userPref", true).findFirst() == null) {
        Currency defaultCurrency = realm.createObject(Currency.class, "GBP");
        defaultCurrency.setUserPref(true);
        defaultCurrency.setResource("R.drawable.gbp");
        realm.copyToRealmOrUpdate(defaultCurrency);
        realm.close();
      }
    });
  }
}
