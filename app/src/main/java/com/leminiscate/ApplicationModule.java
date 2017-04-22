package com.leminiscate;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;

@Module public final class ApplicationModule {

  private final Context mContext;

  ApplicationModule(Context context) {
    mContext = context;
    initDbConfig(context);
  }

  @Provides Context provideContext() {
    return mContext;
  }

  private void initDbConfig(Context context) {
    Realm.init(context);
    RealmConfiguration realmConfiguration =
        new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
    Realm.setDefaultConfiguration(realmConfiguration);
  }
}