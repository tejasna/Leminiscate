package com.leminiscate.login;

import com.leminiscate.data.source.WalletRepositoryComponent;
import com.leminiscate.utils.FragmentScoped;
import dagger.Component;

@FragmentScoped
@Component(dependencies = WalletRepositoryComponent.class, modules = LoginPresenterModule.class)
interface LoginComponent {

  void inject(LoginActivity activity);
}

