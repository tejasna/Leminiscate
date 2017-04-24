package com.leminiscate.data.source;

import android.content.Context;
import com.leminiscate.data.source.local.WalletLocalDataSource;
import com.leminiscate.data.source.remote.WalletRemoteDataSource;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module class WalletRepositoryModule {

  @Singleton @Provides @Local WalletDataSource provideWalletLocalDataSource(Context context) {
    return new WalletLocalDataSource(context);
  }

  @Singleton @Provides @Remote WalletDataSource provideWalletRemoteDataSource(Context context) {
    return new WalletRemoteDataSource(context);
  }
}

