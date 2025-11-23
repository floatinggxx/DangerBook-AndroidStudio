package com.example.DangerBook.data.repository

import com.example.DangerBook.data.local.appointment.AppointmentDao
import com.example.DangerBook.data.local.appointment.AppointmentEntity
import com.example.DangerBook.data.local.barbero.BarberDao
import com.example.DangerBook.data.local.service.ServiceDao
import com.example.DangerBook.data.local.user.UserDao
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

// Lógica de negocio de las citas (versión simplificada)
class CitaRepository(
    private val appointmentDao: AppointmentDao,
    private val userDao: UserDao,
    private val serviceDao: ServiceDao,
    private val barberDao: BarberDao,
    private val disponibilidadRepository: DisponibilidadRepository // ÚNICA dependencia de horarios
) {

    // ... (los métodos createAppointment, getUserAppointments, etc. no cambian)
        suspend fun createAppointment(
        userId: Long,
        barberId: Long?,
        serviceId: Long,
        dateTime: Long,
        durationMinutes: Int,
        notes: String?
    ): Result<Long> {
        return try {
            if (dateTime < System.currentTimeMillis()) {
                return Result.failure(IllegalArgumentException("No puedes agendar citas en el pasado"))
            }
            if (userDao.getById(userId) == null) {
                return Result.failure(IllegalArgumentException("El usuario con ID $userId no existe."))
            }
            if (serviceDao.getById(serviceId) == null) {
                return Result.failure(IllegalArgumentException("El servicio con ID $serviceId no existe."))
            }
            if (barberId != null && barberDao.getById(barberId) == null) {
                return Result.failure(IllegalArgumentException("El barbero con ID $barberId no existe."))
            }
            if (barberId != null) {
                val conflicts = appointmentDao.countConflictingAppointments(barberId, dateTime)
                if (conflicts > 0) {
                    return Result.failure(IllegalArgumentException("Este horario ya está ocupado"))
                }
            }
            val appointment = AppointmentEntity(
                userId = userId,
                barberId = barberId,
                serviceId = serviceId,
                dateTime = dateTime,
                durationMinutes = durationMinutes,
                status = "pending",
                notes = notes
            )
            val id = appointmentDao.insert(appointment)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserAppointments(userId: Long): Flow<List<AppointmentEntity>> {
        return appointmentDao.getByUserId(userId)
    }

    fun getUpcomingAppointments(userId: Long): Flow<List<AppointmentEntity>> {
        return appointmentDao.getUpcomingByUserId(userId)
    }

    suspend fun getAppointmentById(appointmentId: Long): AppointmentEntity? {
        return appointmentDao.getById(appointmentId)
    }

    suspend fun cancelAppointment(appointmentId: Long): Result<Unit> {
        return try {
            appointmentDao.cancelAppointment(appointmentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmAppointment(appointmentId: Long): Result<Unit> {
        return try {
            appointmentDao.confirmAppointment(appointmentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeAppointment(appointmentId: Long): Result<Unit> {
        return try {
            appointmentDao.completeAppointment(appointmentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getBarberAppointments(barberId: Long): Flow<List<AppointmentEntity>> {
        return appointmentDao.getByBarberId(barberId)
    }

    suspend fun getTotalAppointmentsCount(): Int {
        return appointmentDao.count()
    }


    // ===== NUEVA IMPLEMENTACIÓN SIMPLIFICADA =====
    suspend fun getAvailableTimeSlotsForDay(
        barberId: Long,
        date: Calendar,
        serviceDurationMinutes: Int
    ): List<Long> {
        // 1. Formatear la fecha a "yyyy-MM-dd" para la API
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(date.time)

        val availableSlots = mutableListOf<Long>()

        try {
            // 2. Llamar al endpoint del backend que devuelve las horas disponibles
            val hoursResult = disponibilidadRepository.getAvailableHours(barberId, dateString)

            if (hoursResult.isSuccess) {
                val hours = hoursResult.getOrThrow() // Lista de strings: ["09:00", "09:30", ...]
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                // 3. Convertir cada hora a un timestamp completo del día seleccionado
                for (hour in hours) {
                    val startTime = timeFormat.parse(hour)
                    if (startTime != null) {
                        val slotCalendar = date.clone() as Calendar
                        val timeCalendar = Calendar.getInstance().apply { time = startTime }
                        slotCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                        slotCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                        slotCalendar.set(Calendar.SECOND, 0)
                        slotCalendar.set(Calendar.MILLISECOND, 0)

                        val slotTime = slotCalendar.timeInMillis

                        // 4. Añadir a la lista si no es una hora que ya pasó
                        if (slotTime > System.currentTimeMillis()) {
                            availableSlots.add(slotTime)
                        }
                    }
                }
            } else {
                // Si la llamada falla, loguear el error. La función devolverá una lista vacía.
                println("Error al obtener horarios desde la API: ${hoursResult.exceptionOrNull()?.message}")
            }
        } catch (e: Exception) {
            // Capturar cualquier otra excepción (ej. de red)
            e.printStackTrace()
        }

        return availableSlots.sorted() // Devolver la lista (posiblemente vacía)
    }
}