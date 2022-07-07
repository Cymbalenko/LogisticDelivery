package com.example.logisticdelivery.ui.login.common

import androidx.annotation.StringRes

sealed class LoginUiEvent {
    class Loading(val isLoading: Boolean) : LoginUiEvent()
    class Success(val isSuccess: Boolean) : LoginUiEvent()
    class LoginError(@StringRes val errorStringId: Int) : LoginUiEvent()
    class InternetError(val isInternet: Boolean) : LoginUiEvent()
}
