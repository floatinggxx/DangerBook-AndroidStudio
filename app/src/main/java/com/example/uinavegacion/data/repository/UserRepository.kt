package com.example.uinavegacion.data.repository

import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity

//declarar reglas de negocio para el login/register
class UserRepository (
    //inyección del DAO
    private val userDao: UserDao
){
    //orqueste el login
    suspend fun login(email: String, password: String): Result<UserEntity>{
        val user = userDao.getByEmail(email)
        return if(user != null && user.password == password){
            Result.success(user)
        }
        else{
            Result.failure(IllegalArgumentException("Credenciales Inválidas"))
        }
    }

    //orqueste el registro
    suspend fun register(name: String, email: String, phone: String, pass: String): Result<Long>{
        val exists = userDao.getByEmail(email) != null
        if(exists){
            return Result.failure(IllegalArgumentException("Correo ya registrado"))
        }
        else{
            val id = userDao.insert(
                UserEntity(
                    name = name,
                    email = email,
                    phone = phone,
                    password = pass
                )
            )
            return Result.success(id)
        }
    }
}