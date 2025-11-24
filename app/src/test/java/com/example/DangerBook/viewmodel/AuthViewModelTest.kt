package com.example.DangerBook.viewmodel

import com.example.DangerBook.data.repository.UsuarioRepository
import com.example.DangerBook.ui.viewmodel.AuthViewModel
import com.example.DangerBook.ui.viewmodel.LoginUiState
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private lateinit var repository: UsuarioRepository
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `onLoginEmailChange updates email and validates`() {
        val email = "test@example.com"
        viewModel.onLoginEmailChange(email)
        val uiState = viewModel.login.value
        assert(uiState.email == email)
        assert(uiState.emailError == null)
    }

    @Test
    fun `onLoginPassChange updates password`() {
        val password = "password123"
        viewModel.onLoginPassChange(password)
        val uiState = viewModel.login.value
        assert(uiState.pass == password)
    }
}