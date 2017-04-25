package com.leminiscate.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass public class Currency extends RealmObject {

  @PrimaryKey private String name;
  private String resource;
  private Boolean userPref;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public Boolean getUserPref() {
    return userPref;
  }

  public void setUserPref(Boolean userPref) {
    this.userPref = userPref;
  }
}
