package com.leminiscate.data.source.remote;

import com.leminiscate.data.Login;
import io.reactivex.Observable;
import retrofit2.http.POST;

public interface WalletApi {

  @POST("/login")
  Observable<Login> login();
}
