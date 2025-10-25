package com.example.uinavegacion.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.data.local.service.ServiceDao
import com.example.uinavegacion.data.local.service.ServiceEntity
import com.example.uinavegacion.data.local.barber.BarberDao
import com.example.uinavegacion.data.local.barber.BarberEntity
import com.example.uinavegacion.data.local.appointment.AppointmentDao
import com.example.uinavegacion.data.local.appointment.AppointmentEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ServiceEntity::class,
        BarberEntity::class,
        AppointmentEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs
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
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Precarga datos cuando se crea la BD por primera vez
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                preloadData(database)
                            }
                        }
                    })
                        .fallbackToDestructiveMigration() // Borra y recrea si hay cambios
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private suspend fun preloadData(database: AppDatabase) {
            val userDao = database.userDao()
            val serviceDao = database.serviceDao()
            val barberDao = database.barberDao()

            // Usuarios
            if (userDao.count() == 0) {
                val users = listOf(
                    UserEntity(
                        name = "Admin",
                        email = "a@a.cl",
                        phone = "12345678",
                        password = "Admin123!"
                    ),
                    UserEntity(
                        name = "Prueba",
                        email = "prueba@test.cl",
                        phone = "987654321",
                        password = "Prueba123!"
                    )
                )
                users.forEach { userDao.insert(it) }
            }

            // Servicios
            if (serviceDao.count() == 0) {
                val services = listOf(
                    ServiceEntity(
                        name = "Corte Clásico",
                        description = "Corte tradicional con tijera y máquina. Incluye lavado y secado.",
                        price = 15000.0,
                        durationMinutes = 30,
                        isActive = true
                    ),
                    ServiceEntity(
                        name = "Corte Moderno",
                        description = "Corte con estilo actual, degradado y diseños. Incluye lavado.",
                        price = 18000.0,
                        durationMinutes = 45,
                        isActive = true
                    ),
                    ServiceEntity(
                        name = "Barba Completa",
                        description = "Arreglo de barba con máquina y navaja. Incluye toalla caliente.",
                        price = 12000.0,
                        durationMinutes = 30,
                        isActive = true
                    ),
                    ServiceEntity(
                        name = "Corte + Barba",
                        description = "Combo completo: corte de cabello y arreglo de barba.",
                        price = 25000.0,
                        durationMinutes = 60,
                        isActive = true
                    ),
                    ServiceEntity(
                        name = "Afeitado Tradicional",
                        description = "Afeitado clásico con navaja, toalla caliente y productos premium.",
                        price = 15000.0,
                        durationMinutes = 40,
                        isActive = true
                    ),
                    ServiceEntity(
                        name = "Tinte/Color",
                        description = "Aplicación de color o tinte para cabello o barba.",
                        price = 20000.0,
                        durationMinutes = 50,
                        isActive = true
                    )
                )
                serviceDao.insertAll(services)
            }

            // Barberos
            if (barberDao.count() == 0) {
                val barbers = listOf(
                    BarberEntity(
                        name = "Steve Lazaro",
                        specialty = "Cortes clásicos y barba",
                        rating = 4.9,
                        isAvailable = true
                    ),
                    BarberEntity(
                        name = "Nicolas Vallejos",
                        specialty = "Cortes modernos y degradados",
                        rating = 4.8,
                        isAvailable = true
                    ),
                    BarberEntity(
                        name = "Cristian Vega",
                        specialty = "Afeitado tradicional",
                        rating = 5.0,
                        isAvailable = true
                    )
                )
                barberDao.insertAll(barbers)
            }
        }
    }
}