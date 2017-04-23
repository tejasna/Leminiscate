package com.leminiscate.balance;

import com.leminiscate.data.source.WalletRepositoryComponent;
import com.leminiscate.utils.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = WalletRepositoryComponent.class, modules = BalancePresenterModule.class)
interface BalanceComponent {

  void inject(BalanceActivity activity);
}

