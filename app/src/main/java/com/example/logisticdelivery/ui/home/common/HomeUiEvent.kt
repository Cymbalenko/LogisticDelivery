package com.example.logisticdelivery.ui.home.common

import androidx.annotation.StringRes
import com.example.logisticdelivery.ui.login.common.LoginUiEvent

sealed class HomeUiEvent {
    class Loading(val isLoading: Boolean) : HomeUiEvent()
    class Success(val isSuccess: Boolean) : HomeUiEvent()
    class Reload(val isReload: Boolean) : HomeUiEvent()
    class Shift(val isShift: Boolean) : HomeUiEvent()
    class InitHeader(val isInitHeader: Boolean) : HomeUiEvent()
    class LoginError(@StringRes val errorStringId: Int) : HomeUiEvent()
}