package com.example.DangerBook.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.DangerBook.data.local.appointment.AppointmentDao
import com.example.DangerBook.data.local.appointment.AppointmentEntity
import com.example.DangerBook.data.local.barbero.BarberDao
import com.example.DangerBook.data.local.barbero.BarberEntity
import com.example.DangerBook.data.local.service.ServiceDao
import com.example.DangerBook.data.local.service.ServiceEntity
import com.example.DangerBook.data.local.user.UserDao
import com.example.DangerBook.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [
        UserEntity::class,
        ServiceEntity::class,
        BarberEntity::class,
        AppointmentEntity::class
    ],
    version = 1, // Si ya has instalado la app, considera subir la versión a 2
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun serviceDao(): ServiceDao
    abstract fun barberDao(): BarberDao
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val BD_NAME = "dangerbook.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    BD_NAME
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Iniciar corrutina para precargar datos en segundo plano
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).let { database ->
                                    preloadData(database)
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Función suspendida para manejar la precarga de datos
        private suspend fun preloadData(database: AppDatabase) {
            val userDao = database.userDao()
            val serviceDao = database.serviceDao()
            val barberDao = database.barberDao()

            // Ejecutar todas las operaciones en una sola transacción
            database.withTransaction {
                // Precargar usuarios si la tabla está vacía
                if (userDao.count() == 0) {
                    val users = listOf(
                        UserEntity(name = "Admin DangerBook", email = "admin@dangerbook.cl", phone = "912345678", password = "Admin123!", role = "admin"),
                        UserEntity(name = "Carlos Danger", email = "carlos@dangerbook.cl", phone = "987654321", password = "Barber123!", role = "barber"),
                        UserEntity(name = "Miguel Estilo", email = "miguel@dangerbook.cl", phone = "987654322", password = "Barber123!", role = "barber"),
                        UserEntity(name = "Andrés Master", email = "andres@dangerbook.cl", phone = "987654323", password = "Barber123!", role = "barber"),
                        UserEntity(name = "Jose Pérez", email = "jose@test.cl", phone = "987654324", password = "User123!", role = "user"),
                        UserEntity(name = "María González", email = "maria@test.cl", phone = "987654325", password = "User123!", role = "user")
                    )
                    userDao.insertAll(users)
                }

                // Precargar servicios si la tabla está vacía
                if (serviceDao.count() == 0) {
                    val services = listOf(
                        ServiceEntity(name = "Corte Clásico", description = "Corte tradicional con tijera y máquina. Incluye lavado y secado.", price = 15000.0, durationMinutes = 30, isActive = true),
                        ServiceEntity(name = "Corte Moderno", description = "Corte con estilo actual, degradado y diseños. Incluye lavado.", price = 18000.0, durationMinutes = 45, isActive = true),
                        ServiceEntity(name = "Barba Completa", description = "Arreglo de barba con máquina y navaja. Incluye toalla caliente.", price = 12000.0, durationMinutes = 30, isActive = true),
                        ServiceEntity(name = "Corte + Barba", description = "Combo completo: corte de cabello y arreglo de barba.", price = 25000.0, durationMinutes = 60, isActive = true),
                        ServiceEntity(name = "Afeitado Tradicional", description = "Afeitado clásico con navaja, toalla caliente y productos premium.", price = 15000.0, durationMinutes = 40, isActive = true),
                        ServiceEntity(name = "Tinte/Color", description = "Aplicación de color o tinte para cabello o barba.", price = 20000.0, durationMinutes = 50, isActive = true)
                    )
                    serviceDao.insertAll(services)
                }

                // Precargar barberos si la tabla está vacía
                if (barberDao.count() == 0) {
                    val barbers = listOf(
                        BarberEntity(name = "Carlos Danger", specialty = "Cortes clásicos y barba", rating = 4.9, isAvailable = true),
                        BarberEntity(name = "Miguel Estilo", specialty = "Cortes modernos y degradados", rating = 4.8, isAvailable = true),
                        BarberEntity(name = "Andrés Master", specialty = "Afeitado tradicional", rating = 5.0, isAvailable = true)
                    )
                    barberDao.insertAll(barbers)
                }

                // Precargar citas si la tabla está vacía
                if (database.appointmentDao().count() == 0) {
                    val appointments = listOf(
                        // Cita para mañana
                        AppointmentEntity(
                            userId = 5, // Jose Pérez
                            barberId = 1, // Carlos Danger
                            serviceId = 1, // Corte Clásico
                            dateTime = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1); set(Calendar.HOUR_OF_DAY, 10); set(Calendar.MINUTE, 0) }.timeInMillis,
                            durationMinutes = 30,
                            status = "confirmed"
                        ),
                        // Cita para pasado mañana
                        AppointmentEntity(
                            userId = 6, // María González
                            barberId = 2, // Miguel Estilo
                            serviceId = 4, // Corte + Barba
                            dateTime = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2); set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 30) }.timeInMillis,
                            durationMinutes = 60,
                            status = "pending"
                        ),
                        // Cita pasada para el historial
                         AppointmentEntity(
                            userId = 5, // Jose Pérez
                            barberId = 3, // Andrés Master
                            serviceId = 5, // Afeitado Tradicional
                            dateTime = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -5); set(Calendar.HOUR_OF_DAY, 16); set(Calendar.MINUTE, 0) }.timeInMillis,
                            durationMinutes = 40,
                            status = "completed"
                        )
                    )
                    database.appointmentDao().insertAll(appointments)
                }
            }
        }
    }
}