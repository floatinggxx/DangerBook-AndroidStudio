package com.example.DangerBook.data.repository
import com.example.DangerBook.data.remoto.service.UsuarioRemoteModule
import com.example.DangerBook.data.local.user.UserDao
import com.example.DangerBook.data.local.user.UserEntity
import com.example.DangerBook.data.remoto.dto.usuarios.UsuarioDto
import com.example.DangerBook.data.remoto.service.UsuarioApiService
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

// Lógica de negocio de usuarios
class UsuarioRepository(
    private val userDao: UserDao
) {

    private val usuarioApi: UsuarioApiService =
        UsuarioRemoteModule.create(UsuarioApiService::class.java)

    // --- Remote Operations ---

    suspend fun getUsuariosRemotos(): Result<List<UsuarioDto>> {
        return try {
            val usuarios = usuarioApi.findAll()
            Result.success(usuarios)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findUsuarioByIdRemoto(id: Int): Result<UsuarioDto> {
        return try {
            val usuario = usuarioApi.findById(id)
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUsuarioRemoto(usuario: UsuarioDto): Result<UsuarioDto> {
        return try {
            val savedUsuario = usuarioApi.save(usuario)
            Result.success(savedUsuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // --- Local Operations ---

    // Validar credenciales
    suspend fun login(email: String, password: String): Result<UserEntity> {
        return try {
            val credentials = mapOf("email" to email, "contrasena" to password)
            val remoteUser = usuarioApi.login(credentials)

            val localUser = UserEntity(
                id = remoteUser.id_usuario?.toLong() ?: throw IllegalStateException("El ID de usuario remoto no puede ser nulo"),
                name = "${remoteUser.nombre} ${remoteUser.apellido}",
                email = remoteUser.email,
                phone = remoteUser.telefono,
                password = remoteUser.contrasena, // Considerar no guardar la contraseña en texto plano
                role = when (remoteUser.id_rol) {
                    1 -> "admin"
                    2 -> "barber"
                    else -> "user"
                },
                photoUri = null
            )
            userDao.insert(localUser)
            Result.success(localUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Registro: crear nuevo usuario
    suspend fun register(
        name: String,
        apellido: String, // NUEVO
        email: String,
        phone: String,
        pass: String
    ): Result<UsuarioDto> {
        // 1. Crear el objeto DTO para enviar a la API
        val newUserDto = UsuarioDto(
            nombre = name,
            apellido = apellido, // NUEVO
            email = email,
            telefono = phone,
            contrasena = pass,
            fechaRegistro = LocalDateTime.now().toString(),
            id_rol = 3, // Por defecto, rol 'user'
            id_estado = 1 // Por defecto, estado 'activo'
        )

        // 2. Guardar el usuario remotamente
        val remoteResult = saveUsuarioRemoto(newUserDto)

        remoteResult.onSuccess {
            // 3. Si el registro remoto es exitoso, guardar en la base de datos local
            val localUser = UserEntity(
                id = it.id_usuario?.toLong() ?: 0L,
                name = "${it.nombre} ${it.apellido}", // CONCATENAMOS
                email = it.email,
                phone = it.telefono,
                password = it.contrasena, // Considerar no almacenar la contraseña en texto plano
                role = "user"
            )
            userDao.insert(localUser)
        }

        return remoteResult
    }

    // Obtener usuario por ID
    suspend fun getUserById(userId: Long): UserEntity? {
        return userDao.getById(userId)
    }

    // Actualizar usuario completo
    suspend fun updateUser(user: UserEntity): Result<Unit> {
        return try {
            userDao.update(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar solo la foto de perfil
    suspend fun updateUserPhoto(userId: Long, photoUri: String?): Result<Unit> {
        return try {
            userDao.updatePhoto(userId, photoUri)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserName(userId: Long, newName: String): Result<Unit> {
        return try {
            userDao.updateUserName(userId, newName)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserEmail(userId: Long, newEmail: String): Result<Unit> {
        return try {
            userDao.updateUserEmail(userId, newEmail)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserPhone(userId: Long, newPhone: String): Result<Unit> {
        return try {
            userDao.updateUserPhone(userId, newPhone)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserPassword(userId: Long, newPass: String): Result<Unit> {
        return try {
            userDao.updateUserPassword(userId, newPass)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            val response = usuarioApi.resetPassword(mapOf("email" to email))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al resetear la contraseña"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener todos los barberos (Flow para observar cambios)
    fun getAllBarbers(): Flow<List<UserEntity>> {
        return userDao.getAllBarbers()
    }

    // Obtener usuarios por rol
    fun getUsersByRole(role: String): Flow<List<UserEntity>> {
        return userDao.getUsersByRole(role)
    }
}
