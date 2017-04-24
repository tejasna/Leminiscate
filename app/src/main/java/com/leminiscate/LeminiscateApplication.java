package com.leminiscate;

import android.app.Application;
import com.leminiscate.data.source.DaggerWalletRepositoryComponent;
import com.leminiscate.data.source.WalletRepositoryComponent;

public class LeminiscateApplication extends Application {

  private WalletRepositoryComponent mRepositoryComponent;

  @Override public void onCreate() {
    super.onCreate();

    mRepositoryComponent = DaggerWalletRepositoryComponent.builder()
        .applicationModule(new ApplicationModule((getApplicationContext())))
        .build();
  }

  public WalletRepositoryComponent getWalletRepositoryComponent() {
    return mRepositoryComponent;
  }
}
