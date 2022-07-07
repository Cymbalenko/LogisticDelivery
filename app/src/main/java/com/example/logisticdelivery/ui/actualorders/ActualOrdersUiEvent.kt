package com.example.logisticdelivery.ui.actualorders

import androidx.annotation.StringRes
import com.example.logisticdelivery.ui.login.common.LoginUiEvent
import com.example.logisticdelivery.util.LoadingState

sealed class ActualOrdersUiEvent {
    class DisplayLoadingState(val loadingState: LoadingState) : ActualOrdersUiEvent()
    class Success(val isSuccess: Boolean) : ActualOrdersUiEvent()
    class Error(@StringRes val errorStringId: Int) : ActualOrdersUiEvent()
    class Loading(val isLoading: Boolean) : ActualOrdersUiEvent()
    class Update(val isUpdate: Boolean) : ActualOrdersUiEvent()
    class Order(val isOrder: Boolean) : ActualOrdersUiEvent()
    class Confirm(val isOrder: Boolean) : ActualOrdersUiEvent()
    class Restaurant(val isRestaurant: Boolean) : ActualOrdersUiEvent()
    class IsOrders(val IsOrders: Boolean) : ActualOrdersUiEvent()
}