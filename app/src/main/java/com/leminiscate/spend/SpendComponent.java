package com.leminiscate.spend;

import com.leminiscate.data.source.WalletRepositoryComponent;
import com.leminiscate.utils.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = WalletRepositoryComponent.class, modules = SpendPresenterModule.class)
interface SpendComponent {

  void inject(SpendActivity activity);
}
