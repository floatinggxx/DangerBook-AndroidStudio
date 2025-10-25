package com.example.uinavegacion.data.local.barber

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Barberos base de datos
@Dao
interface BarberDao {

    // Insertar múltiples barberos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(barbers: List<BarberEntity>)

    // Insertar un barbero
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(barber: BarberEntity): Long

    // Obtener todos los barberos disponibles ordenados por nombre
    @Query("SELECT * FROM barbers WHERE isAvailable = 1 ORDER BY name ASC")
    fun getAllAvailable(): Flow<List<BarberEntity>>

    // Obtener un barbero por ID
    @Query("SELECT * FROM barbers WHERE id = :barberId")
    suspend fun getById(barberId: Long): BarberEntity?

    // Contar barberos
    @Query("SELECT COUNT(*) FROM barbers")
    suspend fun count(): Int
}