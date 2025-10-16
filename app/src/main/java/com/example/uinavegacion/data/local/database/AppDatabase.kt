package com.example.uinavegacion.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase(){
    //exponer los DAo de las entity que se usaran para insert precargados
    abstract fun userDao(): UserDao

    companion object{
        //variable privada para la instancia de la BD
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val BD_NAME = "ui_navegacion.db"

        //obtener la instancia unica de la BD
        fun getInstance(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                //construir la BD
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    BD_NAME
                )
                //que hacer cuando sea la primera vez que se cree la BD
                    .addCallback(object: RoomDatabase.Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            //corutina para precargamos la info en las tablas
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).userDao()
                                //creamos una semilla para el precargado
                                val seed = listOf(
                                    UserEntity(
                                        name = "Admin",
                                        email = "a@a.cl",
                                        phone = "12345678",
                                        password = "Admin123!"
                                    ),
                                    UserEntity(
                                        name = "Jose",
                                        email = "b@b.cl",
                                        phone = "12345678",
                                        password = "Jose123!"
                                    )

                                )
                                //inserta solo si no hay registros en la tabla
                                if(dao.count() == 0){
                                    seed.forEach { dao.insert(it) }
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
    }
}