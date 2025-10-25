package com.example.uinavegacion.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,
    val email: String,
    val phone: String,
    val password: String,

    // Rol del usuario
    val role: String = "user", // Por defecto es cliente

    // URI de la foto de perfil
    val photoUri: String? = null,

    // Fecha creación
    val createdAt: Long = System.currentTimeMillis()
)

enum class UserRole(val value: String) {
    USER("user"),
    BARBER("barber"),
    ADMIN("admin");

    companion object {
        fun fromString(value: String): UserRole {
            return entries.find { it.value == value } ?: USER
        }
    }
}