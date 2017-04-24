package com.leminiscate.transactions;

import com.leminiscate.data.source.WalletRepositoryComponent;
import com.leminiscate.utils.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = WalletRepositoryComponent.class, modules = TransactionsPresenterModule.class)
interface TransactionsComponent {

  void inject(TransactionsActivity activity);
}

