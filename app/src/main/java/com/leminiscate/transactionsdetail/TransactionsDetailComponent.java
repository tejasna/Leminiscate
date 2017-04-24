package com.leminiscate.transactionsdetail;

import com.leminiscate.data.source.WalletRepositoryComponent;
import com.leminiscate.utils.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = WalletRepositoryComponent.class, modules = TransactionsDetailPresenterModule.class)
interface TransactionsDetailComponent {

  void inject(TransactionsDetailActivity activity);
}
