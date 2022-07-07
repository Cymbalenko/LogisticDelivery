package com.example.logisticdelivery.ui.orderhistory

import com.example.logisticdelivery.util.LoadingState

sealed class OrdersHistoryUiEvent {
    class DisplayLoadingState(val loadingState: LoadingState) : OrdersHistoryUiEvent()
}