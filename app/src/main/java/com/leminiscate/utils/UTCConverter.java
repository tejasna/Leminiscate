package com.leminiscate.utils;

import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class UTCConverter {

  public static Long getTimeInMilliseconds(String utc) {
    DateTimeZone timeZone = DateTimeZone.forID(TimeZone.getDefault().getID());
    DateTime dateTime = new DateTime(utc, timeZone);
    return dateTime.getMillis();
  }
}
