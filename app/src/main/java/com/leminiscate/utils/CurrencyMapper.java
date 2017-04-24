package com.leminiscate.utils;

import com.leminiscate.R;
import com.leminiscate.data.Currency;
import io.realm.Realm;

public class CurrencyMapper {

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

  public static void setDefaultCurrency() {

    Realm.getDefaultInstance().executeTransaction(realm -> {

      if (realm.where(Currency.class).equalTo("preferred", true).findFirst() == null) {
        Currency defaultCurrency = realm.createObject(Currency.class, "GBP");
        defaultCurrency.setPreferred(true);
        defaultCurrency.setResource("R.drawable.gbp");
        realm.copyToRealmOrUpdate(defaultCurrency);
        realm.close();
      }
    });
  }
}
