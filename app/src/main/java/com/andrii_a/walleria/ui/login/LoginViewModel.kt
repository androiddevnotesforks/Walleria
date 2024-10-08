package com.andrii_a.walleria.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrii_a.walleria.domain.ApplicationScope
import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.domain.repository.LoginRepository
import com.andrii_a.walleria.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    photoRepository: PhotoRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    val loginUrl = loginRepository.loginUrl

    val joinUrl = loginRepository.joinUrl

    private val _loginState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.Empty)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _bannerPhoto: MutableStateFlow<Photo?> = MutableStateFlow(null)
    val bannerPhoto: StateFlow<Photo?> = _bannerPhoto.asStateFlow()

    init {
        photoRepository.getRandomPhoto().onEach { result ->
            when (result) {
                is Resource.Empty,
                is Resource.Error,
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    _bannerPhoto.update { result.value }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getAccessToken(code: String) {
        loginRepository.login(code).onEach { backendResult ->
            _loginState.update {
                when (backendResult) {
                    is Resource.Empty -> LoginState.Empty
                    is Resource.Loading -> LoginState.Loading
                    is Resource.Error -> LoginState.Error
                    is Resource.Success -> LoginState.Success(backendResult.value)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Resource.Empty
        ).launchIn(viewModelScope)
    }

    fun retrieveAndSaveUserData(accessToken: AccessToken) {
        applicationScope.launch {
            saveAccessToken(accessToken = accessToken)
            getAndSaveUserProfile()
        }
    }

    private suspend fun saveAccessToken(accessToken: AccessToken) {
        loginRepository.saveAccessToken(accessToken = accessToken)
    }

    private suspend fun getAndSaveUserProfile() {
        loginRepository.getPrivateUserProfile().let { backendResult ->
            if (backendResult is Resource.Success) {
                loginRepository.savePrivateUserProfile(userPrivateProfile = backendResult.value)
            }
        }
    }
}