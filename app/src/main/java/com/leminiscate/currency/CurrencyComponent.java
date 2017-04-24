package com.leminiscate.currency;

import com.leminiscate.data.source.WalletRepositoryComponent;
import com.leminiscate.utils.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = WalletRepositoryComponent.class, modules = CurrencyPresenterModule.class)
interface CurrencyComponent {

  void inject(CurrencyActivity activity);
}