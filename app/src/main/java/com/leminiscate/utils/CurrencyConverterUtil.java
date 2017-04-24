package com.leminiscate.utils;

import com.leminiscate.data.Balance;
import com.leminiscate.data.Currency;
import com.leminiscate.data.Transaction;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyConverterUtil {

  public static double getAmountFromGBPTO(Transaction transaction) {
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

  public static double convertAmountToGBP(Transaction transaction) {
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
  }

  public static Balance convertAmountToPreferredCurrency(Currency currency, String amount) {
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

  // A simple temporary conversion table helper class that holds static values
  // for conversion to standard GBP value as per rates dated 23 April 2016
  // here are the rates
  private static class ConversionTable {
    static final double USD = 1.28;
    static final double JPY = 141.21;
    static final double CHF = 1.27;
    static final double CAD = 1.72;
    static final double GBP = 1;
  }

  public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }
}
