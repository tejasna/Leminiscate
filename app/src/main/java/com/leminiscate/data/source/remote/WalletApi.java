package com.leminiscate.data.source.remote;

import com.leminiscate.data.Balance;
import com.leminiscate.data.Login;
import com.leminiscate.data.Transaction;
import io.reactivex.Observable;
import java.util.List;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

interface WalletApi {

  @POST("/login") Observable<Login> login();

  @GET("/transactions") Observable<List<Transaction>> getTransactions(
      @Header("Authorization") String bearer);

  @GET("/balance") Observable<Balance> getBalance(@Header("Authorization") String bearer);

  @POST("/spend") Observable<Response<Void>> spend(@Header("Authorization") String bearer,
      @Body Transaction transaction);
}
