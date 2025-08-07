package com.example.desafiogalicia.presentation.screens.userdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desafiogalicia.data.model.User
import com.example.desafiogalicia.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserDetailUiState())
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()

    fun loadUserDetail(user: User) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val isFavorite = userRepository.isFavorite(user.login.uuid)
                _uiState.value = _uiState.value.copy(
                    user = user,
                    isFavorite = isFavorite,
                    isLoading = false
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun toggleFavorite(user: User) {
        viewModelScope.launch {
            try {
                val currentIsFavorite = _uiState.value.isFavorite
                if (currentIsFavorite) {
                    userRepository.removeFromFavorites(user.login.uuid)
                } else {
                    userRepository.addToFavorites(user)
                }
                
                _uiState.value = _uiState.value.copy(isFavorite = !currentIsFavorite)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error al actualizar favoritos"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class UserDetailUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false
)