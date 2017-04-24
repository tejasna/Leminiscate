package com.leminiscate;

import android.content.Context;
import com.leminiscate.utils.CurrencyMapper;
import com.leminiscate.widget.FontCache;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

@Module public final class ApplicationModule {

  private final Context mContext;

  ApplicationModule(Context context) {
    mContext = context;
    initDbConfig(context);
    initFont();
    initLogger();
  }

  private void initLogger() {
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree() {
        @Override protected String createStackElementTag(StackTraceElement element) {
          return super.createStackElementTag(element) + ":" + element.getLineNumber();
        }
      });
    }
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

  private void initFont() {
    FontCache.init(provideContext());
  }
}