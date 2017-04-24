package com.leminiscate.utils;

import android.util.Log;
import com.squareup.otto.Bus;
import java.io.IOException;
import okhttp3.Interceptor;

public class ApiErrorImpl implements Interceptor {

  private Bus bus;

  @Override public okhttp3.Response intercept(Chain chain) throws IOException {
    okhttp3.Response response = chain.proceed(chain.request());
    switch (response.code()) {

      case 400:
        error(400, "BAD-REQUEST");
        break;
      case 401:
        error(401, "UNAUTHORIZED");
        break;
      case 402:
        error(402, "PAYMENT-REQUIRED");
        break;
      case 403:
        error(403, "FORBIDDEN");
        break;
      case 404:
        error(404, "NOT-FOUND");
        break;
      case 405:
        error(405, "METHOD NOT ALLOWED");
        break;
      case 406:
        error(406, "NOT-ACCEPTABLE");
        break;
      case 407:
        error(407, "PROXY-AUTHENTICATION-REQUIRED");
        break;
      case 408:
        error(408, "REQUEST_TIMEOUT");
        break;
      case 409:
        error(409, "CONFLICT");
        break;
      case 410:
        error(410, "GONE");
        break;
      case 500:
        error(500, "INTERNAL-SERVER-ERROR");
        break;
      case 501:
        error(501, "INTERNAL-SERVER_ERROR");
        break;
      case 502:
        error(502, "BAD-GATEWAY");
        break;
      case 503:
        error(503, "SERVICE-UNAVAILABLE");
        break;
      case 504:
        error(504, "GATEWAY-TIMEOUT");
        break;
      case 505:
        error(505, "HTTP-VERSION-NOT-SUPPORTED");
        break;
      case 507:
        error(507, "INSUFFICIENT STORAGE");
        break;
      case 508:
        Log.d("RETRO INTERCEPTOR", "LOOP_DETECTED");
        error(508, "LOOP_DETECTED");
        break;
      case 510:
        error(510, "NOT EXTENDED");
        break;
      case 511:
        error(402, "NETWORK-AUTHENTICATION-REQUIRED");
        break;
      default:
        break;
    }
    return response;
  }

  private void error(int code, String message) {

    if (bus == null) {
      bus = BusProvider.getInstance();
    }

    bus.post(new ApiError(code, message));
  }
}

