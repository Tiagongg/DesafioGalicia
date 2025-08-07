package com.example.desafiogalicia.data.repository

import com.example.desafiogalicia.data.local.dao.FavoriteUserDao
import com.example.desafiogalicia.data.local.entity.FavoriteUser
import com.example.desafiogalicia.data.model.*
import com.example.desafiogalicia.data.remote.RandomUserApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserRepositoryTest {

    @Mock
    private lateinit var randomUserApi: RandomUserApi

    @Mock
    private lateinit var favoriteUserDao: FavoriteUserDao

    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        userRepository = UserRepository(randomUserApi, favoriteUserDao)
    }

    @Test
    fun `getUsers returns success when API call is successful`() = runTest {
        // Given
        val mockUser = createMockUser()
        val mockResponse = UserResponse(
            results = listOf(mockUser),
            info = Info("seed", 1, 1, "1.0")
        )
        whenever(randomUserApi.getUsers(10, 1, null, "challenge"))
            .thenReturn(mockResponse)

        // When
        val result = userRepository.getUsers(page = 1, results = 10)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals(mockUser, result.getOrNull()?.first())
    }

    @Test
    fun `getUsers returns failure when API call throws exception`() = runTest {
        // Given
        whenever(randomUserApi.getUsers(10, 1, null, "challenge"))
            .thenThrow(RuntimeException("Network error"))

        // When
        val result = userRepository.getUsers(page = 1, results = 10)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addToFavorites inserts favorite user correctly`() = runTest {
        // Given
        val mockUser = createMockUser()

        // When
        userRepository.addToFavorites(mockUser)

        // Then
        verify(favoriteUserDao).insertFavorite(
            FavoriteUser(
                uuid = "test-uuid",
                fullName = "Mr Test User",
                email = "test@example.com",
                country = "Test Country",
                pictureUrl = "https://example.com/large.jpg"
            )
        )
    }

    @Test
    fun `removeFromFavorites deletes favorite user correctly`() = runTest {
        // Given
        val uuid = "test-uuid"

        // When
        userRepository.removeFromFavorites(uuid)

        // Then
        verify(favoriteUserDao).deleteFavoriteByUuid(uuid)
    }

    @Test
    fun `isFavorite returns correct boolean value`() = runTest {
        // Given
        val uuid = "test-uuid"
        whenever(favoriteUserDao.isFavorite(uuid)).thenReturn(true)

        // When
        val result = userRepository.isFavorite(uuid)

        // Then
        assertTrue(result)
    }

    @Test
    fun `isFavorite returns false when user is not favorite`() = runTest {
        // Given
        val uuid = "test-uuid"
        whenever(favoriteUserDao.isFavorite(uuid)).thenReturn(false)

        // When
        val result = userRepository.isFavorite(uuid)

        // Then
        assertFalse(result)
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