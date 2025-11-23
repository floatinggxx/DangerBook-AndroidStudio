package com.example.DangerBook.data.repository

import com.example.DangerBook.data.local.appointment.AppointmentDao
import com.example.DangerBook.data.local.appointment.AppointmentEntity
import com.example.DangerBook.data.local.barbero.BarberDao
import com.example.DangerBook.data.local.service.ServiceDao
import com.example.DangerBook.data.local.user.UserDao
import com.example.DangerBook.data.remoto.dto.horarios.BloqueDto
import com.example.DangerBook.data.remoto.dto.horarios.DiaDto
import com.example.DangerBook.data.remoto.dto.horarios.DisponibilidadDto
import com.example.DangerBook.data.remoto.dto.horarios.HorarioDto
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

// Lógica de negocio de las citas
class CitaRepository(
    private val appointmentDao: AppointmentDao,
    private val userDao: UserDao,
    private val serviceDao: ServiceDao,
    private val barberDao: BarberDao,
    private val horarioRepository: HorarioRepository,
    private val disponibilidadRepository: DisponibilidadRepository,
    private val bloqueRepository: BloqueRepository,
    private val diaRepository: DiaRepository
) {

    // Crear una nueva cita
    suspend fun createAppointment(
        userId: Long,
        barberId: Long?,
        serviceId: Long,
        dateTime: Long,
        durationMinutes: Int,
        notes: String?
    ): Result<Long> {
        return try {
            // Validar que la fecha no sea en el pasado
            if (dateTime < System.currentTimeMillis()) {
                return Result.failure(IllegalArgumentException("No puedes agendar citas en el pasado"))
            }

            // --- VALIDACIÓN DE CLAVES EXTERNAS ---
            if (userDao.getById(userId) == null) {
                return Result.failure(IllegalArgumentException("El usuario con ID $userId no existe."))
            }
            if (serviceDao.getById(serviceId) == null) {
                return Result.failure(IllegalArgumentException("El servicio con ID $serviceId no existe."))
            }
            if (barberId != null && barberDao.getById(barberId) == null) {
                return Result.failure(IllegalArgumentException("El barbero con ID $barberId no existe."))
            }
            // --- FIN DE VALIDACIÓN ---

            // Verificar si hay conflictos de horario
            if (barberId != null) {
                val conflicts = appointmentDao.countConflictingAppointments(barberId, dateTime)
                if (conflicts > 0) {
                    return Result.failure(IllegalArgumentException("Este horario ya está ocupado"))
                }
            }

            // Crear la cita
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

    // Obtener todas las citas de un usuario
    fun getUserAppointments(userId: Long): Flow<List<AppointmentEntity>> {
        return appointmentDao.getByUserId(userId)
    }

    // Obtener solo las citas próximas
    fun getUpcomingAppointments(userId: Long): Flow<List<AppointmentEntity>> {
        return appointmentDao.getUpcomingByUserId(userId)
    }

    // Obtener una cita específica por ID
    suspend fun getAppointmentById(appointmentId: Long): AppointmentEntity? {
        return appointmentDao.getById(appointmentId)
    }

    // Cancelar una cita
    suspend fun cancelAppointment(appointmentId: Long): Result<Unit> {
        return try {
            appointmentDao.cancelAppointment(appointmentId)
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Confirmar una cita
    suspend fun confirmAppointment(appointmentId: Long): Result<Unit> {
        return try {
            appointmentDao.confirmAppointment(appointmentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Completar una cita
    suspend fun completeAppointment(appointmentId: Long): Result<Unit> {
        return try {
            appointmentDao.completeAppointment(appointmentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener todas las citas de un barbero
    fun getBarberAppointments(barberId: Long): Flow<List<AppointmentEntity>> {
        return appointmentDao.getByBarberId(barberId)
    }
    suspend fun getTotalAppointmentsCount(): Int {
        return appointmentDao.count()
    }

    // Verificar horarios disponibles para un día específico
    suspend fun getAvailableTimeSlotsForDay(
        barberId: Long,
        date: Calendar,
        serviceDurationMinutes: Int
    ): List<Long> {

        val availableSlots = mutableListOf<Long>()

        try {
            // 1. Obtener la disponibilidad del barbero
            val disponibilidades = disponibilidadRepository.findAll().getOrThrow()
            val disponibilidadBarbero = disponibilidades.filter { it.id_usuario == barberId.toInt() }

            // 2. Obtener los horarios para la disponibilidad del barbero
            val horarios = horarioRepository.findAll().getOrThrow()
            val horariosBarbero = horarios.filter { horario -> disponibilidadBarbero.any { it.id_horario == horario.id_horario } }

            // 3. Obtener los días y bloques
            val dias = diaRepository.findAll().getOrThrow()
            val bloques = bloqueRepository.findAll().getOrThrow()

            // 4. Formatear la fecha seleccionada para comparar con el nombre del día
            val dayFormat = SimpleDateFormat("EEEE", Locale("es", "ES"))
            val selectedDayName = dayFormat.format(date.time)

            // 5. Filtrar horarios para el día seleccionado
            val horariosParaDia = horariosBarbero.filter { horario ->
                val dia = dias.find { it.id_dia == horario.id_dia }
                dia?.dia.equals(selectedDayName, ignoreCase = true)
            }

            // 6. Obtener los bloques de tiempo para los horarios filtrados
            val bloquesParaDia = horariosParaDia.mapNotNull { horario ->
                bloques.find { it.id_bloque == horario.id_bloque }
            }

            // 7. Convertir bloques a timestamps y añadir a la lista de slots disponibles
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            for (bloque in bloquesParaDia) {
                val startTime = timeFormat.parse(bloque.fechaInicio)
                if (startTime != null) {
                    val slotCalendar = date.clone() as Calendar
                    val timeCalendar = Calendar.getInstance().apply { time = startTime }
                    slotCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                    slotCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                    slotCalendar.set(Calendar.SECOND, 0)

                    val slotTime = slotCalendar.timeInMillis

                    // Verificar que no colisione con citas existentes
                    val existingAppointments = appointmentDao.getBarberAppointmentsForDay(
                        barberId,
                        slotCalendar.timeInMillis,
                        slotCalendar.timeInMillis + serviceDurationMinutes * 60 * 1000
                    )
                    val hasConflict = existingAppointments.any { appointment ->
                        val appointmentEnd = appointment.dateTime + (appointment.durationMinutes * 60 * 1000)
                        val slotEnd = slotTime + (serviceDurationMinutes * 60 * 1000)
                        slotTime < appointmentEnd && slotEnd > appointment.dateTime
                    }

                    if (!hasConflict && slotTime > System.currentTimeMillis()) {
                        availableSlots.add(slotTime)
                    }
                }
            }

        } catch (e: Exception) {
            // Manejar errores de la API
            e.printStackTrace()
        }

        return availableSlots
    }
}