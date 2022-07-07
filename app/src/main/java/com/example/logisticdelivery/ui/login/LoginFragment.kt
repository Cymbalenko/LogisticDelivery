package com.example.logisticdelivery.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.logisticdelivery.R
import com.example.logisticdelivery.acra.LogisticDeliveryApplication
import com.example.logisticdelivery.ui.login.common.LoginUiEvent
import com.example.logisticdelivery.ui.main.MainActivity
import com.example.logisticdelivery.ui.main.SharedViewModel
import com.example.logisticdelivery.util.Event


class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginEditText = view.findViewById<EditText>(R.id.login_edit_text)
        val passwordEditText = view.findViewById<EditText>(R.id.password_edit_text)
        val loginButton = view.findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            viewModel.onLogin(
                login = loginEditText.text.toString(),
                password = passwordEditText.text.toString()
            )
        }

        viewModel.login.observe(viewLifecycleOwner) { login ->
            sharedViewModel.setLogin(login)
//            navigateToWelcomeFragment(login) // solution with arguments
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) { event ->
            handleEvent(event)
        }
        viewModel.isLogin()
    }

    private fun handleEvent(event: Event<LoginUiEvent>) {
        event.getIfNotHandled()?.let { loginEvent ->
            when (loginEvent) {
                is LoginUiEvent.Loading -> {
                    val progressBar = view?.findViewById<ProgressBar>(R.id.loading_progress_bar)
                    val loginButton = view?.findViewById<Button>(R.id.login_button)

                    progressBar?.isVisible = loginEvent.isLoading
                    loginButton?.isGone = loginEvent.isLoading
                }
                is LoginUiEvent.LoginError -> {
                    Toast.makeText(requireContext(), loginEvent.errorStringId, Toast.LENGTH_LONG)
                        .show()
                }
                is LoginUiEvent.Success -> {
                    Toast.makeText(requireContext(), this.getString(R.string.succes), Toast.LENGTH_LONG)
                    .show()
                     navigateToWelcomeFragment()
                }
                is LoginUiEvent.InternetError -> {
                    if(loginEvent.isInternet){
                        if(!viewModel.isOnline(this.requireContext())){
                            Toast.makeText(requireContext(), this.getString(R.string.network_error), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToWelcomeFragment() {
        val intent = Intent(LogisticDeliveryApplication.instance, MainActivity::class.java)

        startActivity(intent)

    }
}