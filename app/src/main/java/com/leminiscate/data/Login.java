package com.leminiscate.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass public class Login extends RealmObject {

  @SerializedName("token") @Expose @PrimaryKey private String token;

  public String getToken() {
    return token;
  }

  @SuppressWarnings("unused") public void setToken(String token) {
    this.token = token;
  }
}