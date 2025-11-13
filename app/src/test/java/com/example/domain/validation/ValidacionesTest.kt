package com.example.domain.validation

import com.example.DangerBook.domain.validation.validateEmail
import com.example.DangerBook.domain.validation.validateNameLettersOnly
import com.example.DangerBook.domain.validation.validatePhoneDigitsOnly
import com.example.DangerBook.domain.validation.validateStrongPassword
import com.example.DangerBook.domain.validation.validateConfirm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ValidacionesTest {

    @Test
    fun validarEmail_con_formato_correcto(){
        val error = validateEmail("personal@gmail.com")
        assertNull(error)
    }

    @Test
    fun validarEmail_con_email_vacio(){
        val error = validateEmail("")
        assertEquals("El email es obligatorio", error)
    }

    @Test
    fun validarEmail_con_formato_invalido(){
        val error = validateEmail("personal")
        assertEquals("Formato de email inválido", error)
    }

    @Test
    fun validarNombre_con_nombre_correcto(){
        val error = validateNameLettersOnly("Nombre Apellido")
        assertNull(error)
    }

    @Test
    fun validarNombre_con_nombre_vacio(){
        val error = validateNameLettersOnly("")
        assertEquals("El nombre es obligatorio", error)
    }

    @Test
    fun validarNombre_con_numeros(){
        val error = validateNameLettersOnly("Nombre123")
        assertEquals("Solo letras y espacios", error)
    }

    @Test
    fun validarTelefono_con_telefono_correcto(){
        val error = validatePhoneDigitsOnly("123456789")
        assertNull(error)
    }

    @Test
    fun validarTelefono_con_telefono_vacio(){
        val error = validatePhoneDigitsOnly("")
        assertEquals("El teléfono es obligatorio", error)
    }

    @Test
    fun validarTelefono_con_letras(){
        val error = validatePhoneDigitsOnly("12345678a")
        assertEquals("Solo números", error)
    }

    @Test
    fun validarTelefono_con_telefono_corto(){
        val error = validatePhoneDigitsOnly("123")
        assertEquals("Debe tener entre 8 y 15 dígitos", error)
    }

    @Test
    fun validarContrasenaFuerte_con_contrasena_correcta(){
        val error = validateStrongPassword("Password123*")
        assertNull(error)
    }

    @Test
    fun validarContrasenaFuerte_con_contrasena_vacia(){
        val error = validateStrongPassword("")
        assertEquals("La contraseña es obligatoria", error)
    }

    @Test
    fun validarContrasenaFuerte_con_contrasena_corta(){
        val error = validateStrongPassword("Pass1*")
        assertEquals("Mínimo 8 caracteres", error)
    }

    @Test
    fun validarContrasenaFuerte_sin_mayuscula(){
        val error = validateStrongPassword("password123*")
        assertEquals("Debe incluir una mayúscula", error)
    }

    @Test
    fun validarContrasenaFuerte_sin_minuscula(){
        val error = validateStrongPassword("PASSWORD123*")
        assertEquals("Debe incluir una minúscula", error)
    }

    @Test
    fun validarContrasenaFuerte_sin_numero(){
        val error = validateStrongPassword("Password*")
        assertEquals("Debe incluir un número", error)
    }

    @Test
    fun validarContrasenaFuerte_sin_simbolo(){
        val error = validateStrongPassword("Password123")
        assertEquals("Debe incluir un símbolo", error)
    }

    @Test
    fun validarContrasenaFuerte_con_espacios(){
        val error = validateStrongPassword("Password 123*")
        assertEquals("No debe contener espacios", error)
    }

    @Test
    fun validarConfirmacion_con_contrasenas_que_coinciden(){
        val error = validateConfirm("Password123*", "Password123*")
        assertNull(error)
    }

    @Test
    fun validarConfirmacion_con_confirmacion_vacia(){
        val error = validateConfirm("Password123*", "")
        assertEquals("Confirma tu contraseña", error)
    }

    @Test
    fun validarConfirmacion_con_contrasenas_que_no_coinciden(){
        val error = validateConfirm("Password123*", "Password123")
        assertEquals("Las contraseñas no coinciden", error)
    }
}