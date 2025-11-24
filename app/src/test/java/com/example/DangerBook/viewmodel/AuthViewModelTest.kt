package com.example.DangerBook.viewmodel

import com.example.DangerBook.data.repository.UsuarioRepository
import com.example.DangerBook.ui.viewmodel.AuthViewModel
import com.example.DangerBook.util.MainCoroutineRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AuthViewModel
    private lateinit var repository: UsuarioRepository

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        viewModel = AuthViewModel(repository)
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