package com.leminiscate.data.source;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.realm.RealmObject;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetModule {

  private Retrofit retrofit;
  private static final String BASE_URL = "https://interviewer-api.herokuapp.com";

  public NetModule() {
    retrofit = retrofitBuilder(gsonBuilder());
  }

  public Retrofit getRetrofit() {
    return retrofit;
  }

  private Retrofit retrofitBuilder(Gson gson) {

    return new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
         .baseUrl(BASE_URL)
        .client(httpClientBuilder().build())
        .build();
  }

  private OkHttpClient.Builder httpClientBuilder() {

    HttpLoggingInterceptor bodyInterceptor = new HttpLoggingInterceptor();
    bodyInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    httpClient.connectTimeout(30, TimeUnit.SECONDS);
    httpClient.readTimeout(30, TimeUnit.SECONDS);
    httpClient.writeTimeout(30, TimeUnit.SECONDS);
    addPreRequisiteHeaders(httpClient);
    httpClient.addInterceptor(bodyInterceptor);

    return httpClient;
  }

  private void addPreRequisiteHeaders(OkHttpClient.Builder httpClient) {

    httpClient.addInterceptor(chain -> {
      Request request;
      request = chain.request()
          .newBuilder()
          .addHeader("Accept", "application/json")
          .addHeader("Content-Type", "application/json")
          .build();
      return chain.proceed(request);
    });
  }

  private Gson gsonBuilder() {

    return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
        .setExclusionStrategies(new ExclusionStrategy() {
          @Override public boolean shouldSkipField(FieldAttributes f) {
            return f.getDeclaringClass().equals(RealmObject.class);
          }

          @Override public boolean shouldSkipClass(Class<?> clazz) {
            return false;
          }
        })
        .create();
  }
}
