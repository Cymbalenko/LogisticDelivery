package com.example.logisticdelivery.ui.login

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.example.logisticdelivery.R
import com.example.logisticdelivery.acra.LogisticDeliveryApplication
import com.example.logisticdelivery.model.repository.Repository
import com.example.logisticdelivery.model.repository.RoomRepository
import com.example.logisticdelivery.ui.login.common.LoginUiEvent
import com.example.logisticdelivery.ui.properties.AppProperties
import com.example.logisticdelivery.util.Event
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {

    private val _login = MutableLiveData<String>()
    //    private val _loginErrorEvent = MutableLiveData<Event<Int>>()
//    private val _loadingEvent = MutableLiveData<Event<Boolean>>()
    private val _uiEvent = MutableLiveData<Event<LoginUiEvent>>()
    private val preferences = PreferenceManager.getDefaultSharedPreferences(
        LogisticDeliveryApplication.instance)
    val login: LiveData<String> = _login
    //    val loginErrorEvent: LiveData<Event<Int>> = _loginErrorEvent
//    val loadingEvent: LiveData<Event<Boolean>> = _loadingEvent
    val uiEvent: LiveData<Event<LoginUiEvent>> = _uiEvent

    fun onLogin(login: String, password: String) {
        // launches a coroutine in the ViewModel and prevents possible leaks from the coroutine
        viewModelScope.launch {
            _uiEvent.value = Event(LoginUiEvent.Loading(true))

            // Fake work to see the loading
            delay(2000) // can only be called inside a coroutine
            if (login==""&&password==""){
                IsAvtorization("5405","5405")
            }else{
                IsAvtorization(login,password)
            }
        }

    }
    fun isLogin(){
        val staff = preferences.getString("pref_driver_staffId","")
        val pass =  preferences.getString("pref_driver_password","")
        staff?.let {
            pass?.let {
                if (staff!=""&&pass!=""){
                    _uiEvent.value = Event(LoginUiEvent.Loading(true))
                    IsAvtorization(staff,pass)
                }
            }
        }

    }
    fun isOnline(context: Context):Boolean{
        var check=false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.let { manager->
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                val capabilities =
                    manager.getNetworkCapabilities(connectivityManager.activeNetwork)
                capabilities?.let {
                    if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        check=true
                    } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        check=true
                    } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        check=true
                    }else{
                        check=false
                    }
                }
            }else{
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                activeNetworkInfo?.let {
                    if (it.isConnected){
                        check=true
                    }else{
                        check=false
                    }
                }
            }

        }
        return check
    }
    fun IsAvtorization(login:String,password:String) {

        _uiEvent.value = Event(LoginUiEvent.InternetError(true))
        try {
            Repository.autorization(AppProperties.login_api, AppProperties.password_api)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ token ->
                    AppProperties.token = token
                    Repository.isRegister(login,password, "Bearer " + AppProperties.token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ check ->
                            if(check){
                                Repository.getDriver(login, "Bearer " + AppProperties.token)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ driverApi->
                                        _login.value = login
                                        _uiEvent.value=Event(LoginUiEvent.Success(true))
                                        preferences.edit{putString("pref_driver_staffId",login)}
                                        preferences.edit{putString("pref_driver_password",password)}
                                        RoomRepository.isDriver(login)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({ driver->
                                                AppProperties.selectDriverId=driver.driverId
                                                AppProperties.selectDriverLogin=login
                                                AppProperties.selectDriverPassword=password
                                                driver.fullName?.let{
                                                    AppProperties.selectDriverName=it
                                                }
                                                 driver.phone?.let {
                                                     AppProperties.selectDriverPhone=it
                                                 }
                                                driver.driverType.name.let {
                                                    AppProperties.selectDriverType=it
                                                }

                                                driverApi.driverStatus.value.let {
                                                    AppProperties.selectDriverStatus=it.toInt()
                                                }
                                                Log.e("MainViewModeltoken", "${driver}")
                                                Log.e("myTimerDriverStatus", "${driverApi.driverStatus.value}")
                                                RoomRepository.updateDriver(driverApi)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe({
                                                        _uiEvent.value = Event(LoginUiEvent.Loading(false))
                                                    }, { excep ->
                                                        _uiEvent.value = Event(LoginUiEvent.Loading(false))
                                                        Log.e("MainViewModeltoken", "Succes Driver Not Found ROOM")

                                                    })
                                            }, { excep ->

                                                AppProperties.selectDriverId=driverApi.driverId
                                                AppProperties.selectDriverLogin=login
                                                AppProperties.selectDriverPassword=password
                                                driverApi.fullName?.let{
                                                    AppProperties.selectDriverName=it
                                                }
                                                driverApi.phone?.let {
                                                    AppProperties.selectDriverPhone=it
                                                }
                                                driverApi.driverType.name.let {
                                                    AppProperties.selectDriverType=it
                                                }
                                                driverApi.driverStatus.value.let {
                                                    AppProperties.selectDriverStatus=it.toInt()
                                                } 

                                                Log.e("MainViewModeltoken", "${driverApi}")
                                                RoomRepository.insertDriver(driverApi)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe({ ress->
                                                        _uiEvent.value = Event(LoginUiEvent.Loading(false))
                                                    }, { excep ->
                                                        _uiEvent.value = Event(LoginUiEvent.Loading(false))
                                                        Log.e("MainViewModeltoken", "Succes Driver Not Found ROOM")

                                                    })

                                                Log.e("MainViewModeltoken", "Succes Driver Not Found ROOM")

                                            })

                                    }, { excep ->
                                        _uiEvent.value = Event(LoginUiEvent.Loading(false))
                                        _uiEvent.value = Event(LoginUiEvent.LoginError(R.string.error_driver_not_found))
                                        Log.e("MainViewModeltoken", "Error Driver Not Found")
                                        //_uiEvent.value = Event(LoginUiEvent.LoginError(R.string.error_login_incorrect_credentials))
                                    })

                            }else{
                                _uiEvent.value = Event(LoginUiEvent.Loading(false))
                                Log.e("MainViewModeltoken", "Succes Driver Not Found")
                                _uiEvent.value = Event(LoginUiEvent.LoginError(R.string.error_login_incorrect_credentials))
                            }
                        }, { exception ->
                            _uiEvent.value = Event(LoginUiEvent.Loading(false))
                            Log.e("MainViewModeltoken", "Error isavtorization Api\n" + exception.message.toString())
                            _uiEvent.value = Event(LoginUiEvent.LoginError(R.string.error_login_incorrect_credentials))
                        })
                    Log.d("MainViewModeltoken", "Success avtorization Api\n" + token)
                }, { throwable ->
                    _uiEvent.value = Event(LoginUiEvent.Loading(false))
                    Log.e("MainViewModeltoken", "Error avtorization Api\n" + throwable.message.toString())
                })


        }catch (e:Exception){
            _uiEvent.value = Event(LoginUiEvent.Loading(false))
        }
    }
}