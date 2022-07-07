package com.example.logisticdelivery.ui.main.common

import com.example.logisticdelivery.ui.login.common.LoginUiEvent

sealed class MainUiEvent {
    class Loading(val isLoading: Boolean) : MainUiEvent()
    class Reload(val isReload: Boolean) : MainUiEvent()
    class Success(val isSuccess: Boolean) : MainUiEvent()
}