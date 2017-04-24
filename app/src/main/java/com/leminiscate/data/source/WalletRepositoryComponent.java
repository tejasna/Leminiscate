package com.leminiscate.data.source;

import com.leminiscate.ApplicationModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton @Component(modules = { WalletRepositoryModule.class, ApplicationModule.class })
public interface WalletRepositoryComponent {

  WalletRepository getWalletRepository();
}

