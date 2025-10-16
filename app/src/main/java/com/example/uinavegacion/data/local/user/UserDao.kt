package com.example.uinavegacion.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    //insertar un nuevo user en la tabla
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    //obtener los datos de un usuario mediante su email
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    //obtener todos los usuarios de la tabla ordenados por id ascendente
    @Query("SELECT * FROM users ORDER BY id ASC")
    suspend fun getAll(): List<UserEntity>

    //obtener la cantidad de registros en la tabla
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}