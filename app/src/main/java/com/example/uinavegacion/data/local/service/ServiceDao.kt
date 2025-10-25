package com.example.uinavegacion.data.local.service

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// DAO para servicios
@Dao
interface ServiceDao {

    // Insertar múltiples servicios
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<ServiceEntity>)

    // Insertar un servicio
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(service: ServiceEntity): Long

    // Obtener todos los servicios activos ordenados por nombre
    @Query("SELECT * FROM services WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActive(): Flow<List<ServiceEntity>>

    // Obtener un servicio por ID
    @Query("SELECT * FROM services WHERE id = :serviceId")
    suspend fun getById(serviceId: Long): ServiceEntity?

    // Contar servicios
    @Query("SELECT COUNT(*) FROM services")
    suspend fun count(): Int
}