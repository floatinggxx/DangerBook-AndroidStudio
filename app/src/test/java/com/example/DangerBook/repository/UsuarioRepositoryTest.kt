package com.example.DangerBook.repository

import com.example.DangerBook.data.local.user.UserDao
import com.example.DangerBook.data.remoto.dto.usuarios.UsuarioDto
import com.example.DangerBook.data.remoto.service.UsuarioApiService
import com.example.DangerBook.data.repository.UsuarioRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UsuarioRepositoryTest {

    private lateinit var repository: UsuarioRepository
    private lateinit var userDao: UserDao
    private lateinit var apiService: UsuarioApiService

    @Before
    fun setUp() {
        userDao = mockk(relaxed = true)
        apiService = mockk(relaxed = true)
        repository = UsuarioRepository(userDao)

        val apiField = repository.javaClass.getDeclaredField("usuarioApi")
        apiField.isAccessible = true
        apiField.set(repository, apiService)
    }

    @Test
    fun `login success`() = runTest {
        val email = "test@example.com"
        val password = "password"
        val userDto = UsuarioDto(
            id_usuario = 1,
            nombre = "Test",
            apellido = "User",
            email = email,
            telefono = "123456789",
            contrasena = password,
            fechaRegistro = "2024-01-01T12:00:00",
            id_estado = 1,
            id_rol = 3
        )

        coEvery { apiService.login(any()) } returns userDto

        val result = repository.login(email, password)

        assert(result.isSuccess)
        coVerify { userDao.insert(any()) }
    }
}