package com.example.dangerbook_grupo2.domain.validation

import android.util.Patterns

// Esta validación hace que el usuario no ingrese números sino espacios y letras
fun validateNameLettersOnly(nombre:String):String?{
    if(nombre.isBlank()) return "El nombre es obligatorio"
    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")
    return if(!regex.matches(nombre)) "Solo se permiten letras y espacios" else null
}

// Esta validación utiliza el patrón de correo electrónico para verificar si el correo es válido o no
fun validateEmail(email:String):String?{
    if(email.isBlank()) return "El correo es obligatorio"
    val ok = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return if(!ok) "Formato de correo inválido" else null
}

//validar telefono 8 y 9 digitos de longitud, no vacio, solo numeros
fun validatePhoneDigitsOnly(phone: String):String?{
    if(phone.isBlank()) return "El teléfono es obligatorio"
    if(!phone.all { it.isDigit() }) return "Solo se permiten números"
    if(phone.length !in 8 .. 9) return "Debe tener una longitud entre 8 y 9 dígitos"
    return null
}

//validaciones de la clave: no vacio, numero,mayus,minus,simbolo
fun validateStrongPassword(pass: String): String?{
    if(pass.isBlank()) return "Debe escribir una contraseña"
    if(pass.length < 8) return "Debe tener al menos 8 caracteres"
    if(!pass.any { it.isUpperCase() }) return "Debe contener al menos una mayúscula"
    if(!pass.any { it.isLowerCase() }) return "Debe contener al menos una minúscula"
    if(!pass.any { it.isDigit() }) return "Debe contener al menos un número"
    if(!pass.any { it.isLetterOrDigit() }) return "Debe incluir un símbolo"
    if(pass.contains(' ')) return "No debe tener espacios"
    return null
}

//validacion para coincidir las claves
fun validateConfirm(pass: String, confirm: String): String?{
    if(confirm.isBlank()) return "Debe confirmar su contraseña"
    return if(pass != confirm) "Las contraseñas no coinciden" else null
}
