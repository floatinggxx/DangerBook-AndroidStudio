package com.example.uinavegacion.data.local.appointment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// DAO pa las citas
@Dao
interface AppointmentDao {

    // Insertar una nueva cita
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(appointment: AppointmentEntity): Long

    // Actualizar una cita
    @Update
    suspend fun update(appointment: AppointmentEntity)

    // Obtener todas las citas de un usuario ordenadas por fecha
    @Query("SELECT * FROM appointments WHERE userId = :userId ORDER BY dateTime DESC")
    fun getByUserId(userId: Long): Flow<List<AppointmentEntity>>

    // Obtener citas pendientes/confirmadas de un usuario
    @Query("""
        SELECT * FROM appointments 
        WHERE userId = :userId 
        AND status IN ('pending', 'confirmed')
        ORDER BY dateTime ASC
    """)
    fun getUpcomingByUserId(userId: Long): Flow<List<AppointmentEntity>>

    // Obtener una cita por ID
    @Query("SELECT * FROM appointments WHERE id = :appointmentId")
    suspend fun getById(appointmentId: Long): AppointmentEntity?

    // Verificar si un barbero tiene citas en un horario específico
    @Query("""
        SELECT COUNT(*) FROM appointments 
        WHERE barberId = :barberId 
        AND dateTime = :dateTime 
        AND status IN ('pending', 'confirmed')
    """)
    suspend fun countConflictingAppointments(barberId: Long, dateTime: Long): Int

    // Obtener todas las citas de un día específico para un barbero
    @Query("""
        SELECT * FROM appointments 
        WHERE barberId = :barberId 
        AND dateTime >= :startOfDay 
        AND dateTime < :endOfDay
        AND status IN ('pending', 'confirmed')
        ORDER BY dateTime ASC
    """)
    suspend fun getBarberAppointmentsForDay(barberId: Long, startOfDay: Long, endOfDay: Long): List<AppointmentEntity>

    // Cancelar una cita
    @Query("UPDATE appointments SET status = 'cancelled', updatedAt = :timestamp WHERE id = :appointmentId")
    suspend fun cancelAppointment(appointmentId: Long, timestamp: Long = System.currentTimeMillis())

    // Contar citas
    @Query("SELECT COUNT(*) FROM appointments")
    suspend fun count(): Int
}