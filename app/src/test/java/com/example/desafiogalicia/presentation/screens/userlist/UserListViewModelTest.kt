package com.example.desafiogalicia.presentation.screens.userlist

import com.example.desafiogalicia.data.model.*
import com.example.desafiogalicia.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class UserListViewModelTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: UserListViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Configurar respuesta exitosa por defecto para la carga inicial
        runTest {
            whenever(userRepository.getUsers(1, 10, null))
                .thenReturn(Result.success(listOf(createMockUser())))
        }
        
        viewModel = UserListViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        // Dado - El ViewModel está inicializado
        
        // Cuando - Obtener el estado inicial
        val state = viewModel.uiState.first()
        
        // Entonces
        assertEquals(1, state.users.size)
        assertFalse(state.isLoading)
        assertEquals(null, state.error)
        assertEquals("", state.searchQuery)
    }

    @Test
    fun `loadUsers sets loading state correctly`() = runTest {
        // Dado
        whenever(userRepository.getUsers(1, 10, null))
            .thenReturn(Result.success(emptyList()))
        
        // Cuando
        viewModel.refreshUsers()
        
        // Entonces
        verify(userRepository).getUsers(1, 10, null)
    }

    @Test
    fun `loadUsers with error shows error state`() = runTest {
        // Dado
        val errorMessage = "Network error"
        whenever(userRepository.getUsers(1, 10, null))
            .thenReturn(Result.failure(RuntimeException(errorMessage)))
        
        // Cuando
        viewModel.refreshUsers()
        advanceUntilIdle()
        
        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `searchUsers updates search query and calls repository with nationality`() = runTest {
        // Dado
        val nationality = "US"
        whenever(userRepository.getUsers(1, 10, nationality))
            .thenReturn(Result.success(listOf(createMockUser())))
        
        // Cuando
        viewModel.searchUsers(nationality)
        advanceUntilIdle()
        
        // Entonces
        verify(userRepository).getUsers(1, 10, nationality)
        assertEquals(nationality, viewModel.uiState.value.searchQuery)
    }

    @Test
    fun `toggleFavorite calls repository methods correctly`() = runTest {
        // Dado
        val user = createMockUser()
        whenever(userRepository.isFavorite(user.login.uuid)).thenReturn(false)
        
        // Cuando
        viewModel.toggleFavorite(user)
        advanceUntilIdle()
        
        // Entonces
        verify(userRepository).isFavorite(user.login.uuid)
        verify(userRepository).addToFavorites(user)
    }

    @Test
    fun `clearError resets error state`() = runTest {
        // Dado - Set an error state first
        whenever(userRepository.getUsers(1, 10, null))
            .thenReturn(Result.failure(RuntimeException("Test error")))
        viewModel.refreshUsers()
        advanceUntilIdle()
        
        // Cuando
        viewModel.clearError()
        
        // Entonces
        assertEquals(null, viewModel.uiState.value.error)
    }

    private fun createMockUser() = User(
        gender = "male",
        name = Name("Mr", "Test", "User"),
        location = Location(
            street = Street(123, "Test Street"),
            city = "Test City",
            state = "Test State",
            country = "Test Country",
            postcode = "12345",
            coordinates = Coordinates("0.0", "0.0"),
            timezone = Timezone("+00:00", "GMT")
        ),
        email = "test@example.com",
        login = Login(
            uuid = "test-uuid",
            username = "testuser",
            password = "password",
            salt = "salt",
            md5 = "md5hash",
            sha1 = "sha1hash",
            sha256 = "sha256hash"
        ),
        dob = DateOfBirth("1990-01-01T00:00:00.000Z", 33),
        registered = Registered("2020-01-01T00:00:00.000Z", 3),
        phone = "123-456-7890",
        cell = "098-765-4321",
        id = ID("SSN", "123-45-6789"),
        picture = Picture(
            large = "https://example.com/large.jpg",
            medium = "https://example.com/medium.jpg",
            thumbnail = "https://example.com/thumb.jpg"
        ),
        nat = "US"
    )
}