package com.example.logisticdelivery.ui.main

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.logisticdelivery.R
import com.example.logisticdelivery.model.repository.Repository
import com.example.logisticdelivery.model.repository.RoomRepository
import com.example.logisticdelivery.ui.home.common.HomeUiEvent
import com.example.logisticdelivery.ui.login.common.LoginUiEvent
import com.example.logisticdelivery.ui.main.common.MainUiEvent
import com.example.logisticdelivery.ui.properties.AppProperties
import com.example.logisticdelivery.util.Event
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class SharedViewModel : ViewModel() {

    private val _login = MutableLiveData<String>()
    private val _uiEvent = MutableLiveData<Event<MainUiEvent>>()
    val login: LiveData<String> = _login
    val uiEvent: LiveData<Event<MainUiEvent>> = _uiEvent
    fun setLogin(login: String) {
        _login.value = login
        Log.d("SharedViewModel", this.login.value.toString())
    }
    fun InitTimer(){
        try {
            Handler().postDelayed({
                _uiEvent.value = Event(MainUiEvent.Reload(true))
                Log.d("MainViewModeltoken", "InitHeader")
            }, 1000)

        }catch (e:Exception){
            Log.d("MainViewModeltoken", "timerException")
        }

    }
    fun changePhoneNumber(phone:String){
        try {
            Repository.autorization(AppProperties.login_api, AppProperties.password_api)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ token ->
                        AppProperties.token = token
                        if(AppProperties.selectDriverId!=""){
                            Repository.setDriverPhone(AppProperties.selectDriverId,phone, "Bearer " + AppProperties.token)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ check ->
                                    RoomRepository.updateByDriverIdPhone(phone,AppProperties.selectDriverId)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({
                                            }, { excep ->
                                                Log.e("MainViewModeltoken", "Succes Driver Not Found ROOM")

                                            })
                                }, { exception ->
                                    Log.e("MainViewModeltoken", "Error isavtorization Api\n" + exception.message.toString())
                                })
                        Log.d("MainViewModeltoken", "Success avtorization Api\n" + token)


                        }
                    }, { throwable ->
                        Log.e("MainViewModeltoken", "Error avtorization Api\n" + throwable.message.toString())
                    })


        }catch (e:Exception){
        }
    }
}