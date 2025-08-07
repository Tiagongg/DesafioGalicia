package com.example.desafiogalicia.presentation.screens.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.desafiogalicia.data.model.User
import com.example.desafiogalicia.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserListUiState())
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    init {
        loadUsers()
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            userRepository.getAllFavorites().collect { favoriteUsers ->
                val favoriteUuids = favoriteUsers.map { it.uuid }.toSet()
                _uiState.value = _uiState.value.copy(favoriteUsers = favoriteUuids)
            }
        }
    }

    fun loadUsers(nationality: String? = null, isRefresh: Boolean = false) {
        if (isLoading) return

        viewModelScope.launch {
            if (isRefresh) {
                currentPage = 1
                isLastPage = false
            }
            
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            isLoading = true

            userRepository.getUsers(
                page = currentPage,
                results = 10,
                nationality = nationality
            ).fold(
                onSuccess = { newUsers ->
                    _uiState.value = _uiState.value.copy(
                        users = newUsers,  // Siempre reemplazar, no agregar
                        isLoading = false,
                        error = null,
                        currentPage = currentPage,
                        hasNextPage = newUsers.size == 10,  // Si recibimos 10, puede haber más
                        hasPreviousPage = currentPage > 1
                    )
                    
                    isLastPage = newUsers.size < 10
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error desconocido"
                    )
                }
            )

            isLoading = false
        }
    }

    fun goToNextPage() {
        if (_uiState.value.hasNextPage && !isLoading) {
            currentPage++
            loadUsers(_uiState.value.searchQuery)
        }
    }
    
    fun goToPreviousPage() {
        if (_uiState.value.hasPreviousPage && !isLoading) {
            currentPage--
            loadUsers(_uiState.value.searchQuery)
        }
    }

    fun searchUsers(nationality: String) {
        _uiState.value = _uiState.value.copy(searchQuery = nationality)
        loadUsers(nationality = nationality.ifBlank { null }, isRefresh = true)
    }

    fun toggleFavorite(user: User) {
        viewModelScope.launch {
            try {
                val isFavorite = userRepository.isFavorite(user.login.uuid)
                if (isFavorite) {
                    userRepository.removeFromFavorites(user.login.uuid)
                } else {
                    userRepository.addToFavorites(user)
                }
                // Nota: La UI se actualizará automáticamente a través de observeFavorites()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error al actualizar favoritos"
                )
            }
        }
    }



    fun refreshUsers() {
        loadUsers(_uiState.value.searchQuery, isRefresh = true)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class UserListUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val favoriteUsers: Set<String> = emptySet(),
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val hasNextPage: Boolean = false,
    val hasPreviousPage: Boolean = false
)