package com.example.DangerBook.data.repository
import com.example.DangerBook.data.local.user.UserDao
import com.example.DangerBook.data.local.user.UserEntity
import kotlinx.coroutines.flow.Flow

// Lógica de negocio de usuarios
class UserRepository(
    private val userDao: UserDao
) {

    // Validar credenciales
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null && user.password == password) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales Inválidas"))
        }
    }

    // Registro: crear nuevo usuario
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        pass: String,
        role: String = "user",
        photoUri: String? = null
    ): Result<Long> {
        val exists = userDao.getByEmail(email) != null
        if (exists) {
            return Result.failure(IllegalArgumentException("Correo ya registrado"))
        }

        val id = userDao.insert(
            UserEntity(
                name = name,
                email = email,
                phone = phone,
                password = pass,
                role = role,
                photoUri = photoUri
            )
        )
        return Result.success(id)
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

    // Obtener todos los barberos (Flow para observar cambios)
    fun getAllBarbers(): Flow<List<UserEntity>> {
        return userDao.getAllBarbers()
    }

    // Obtener usuarios por rol
    fun getUsersByRole(role: String): Flow<List<UserEntity>> {
        return userDao.getUsersByRole(role)
    }
}