package com.example.dangerbook_grupo2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dangerbook_grupo2.domain.validation.validateConfirm
import com.example.dangerbook_grupo2.domain.validation.validateEmail
import com.example.dangerbook_grupo2.domain.validation.validateNameLettersOnly
import com.example.dangerbook_grupo2.domain.validation.validatePhoneDigitsOnly
import com.example.dangerbook_grupo2.domain.validation.validateStrongPassword
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


//estrctura de datos para el form de login
data class LoginUiState(
    //variables para manipular los campos del formulario
    val email: String = "",
    val pass: String = "",
    //variables para el manejo de errores de sus validaciones
    val emailError: String? = "",
    val passError: String? = "",
    //variables para los elementos del formulario
    val isSubmitting: Boolean = false, //flag de carga
    val canSubmit: Boolean = false, //visibilidad de boton login
    val success: Boolean = false, //valida formulario ok
    val errorMsg: String? = "" //error general (usuario o clave incorrecta)
)

//estructura de datos para el form de registro
data class RegisterUiState(
    //variables para guardar los campos del formulario
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val pass: String = "",
    val confirm: String = "",
    //variables para el manejo de errores
    val nameError: String? = "",
    val emailError: String? = "",
    val phoneError: String? = "",
    val passError: String? = "",
    val confirmError: String? = "",
    //variables generales del form
    val isSubmitting: Boolean = false, //flag de carga
    val canSubmit: Boolean = false, //visibilidad de boton registrar
    val success: Boolean = false, //valida formulario ok
    val errorMsg: String? = "" //error general (usuario ya existente)
)

//estructura para los objetos de los usuarios
private data class DemoUser(
    val name: String,
    val email: String,
    val phone: String,
    val pass: String
)
//viewmodel que maneja login/register
class AuthViewModel: ViewModel(){
    //coleccion estatica de datos compartida entre ambas instancias
    companion object{
        private val USERS = mutableListOf(
            //usuario por defecto
            DemoUser("User","a@a.cl","12345678","User123!")
        )
    }

    //flujo de estados para observar desde la UI
    private val _login = MutableStateFlow(LoginUiState())
    var login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    var register: StateFlow<RegisterUiState> = _register

    //Funcion para habilitar el botón de entrar en el login
    fun recomputeLoginCanSubmit(){
        val s = _login.value
        val can = s.emailError == null && s.email.isNotBlank()
                && s.pass.isNotBlank()  && s.passError == null
        _login.update { it.copy(canSubmit = can) }
    }

    // Funciones para manipular los cambios de estado del Login
    fun onLoginEmailChange(value: String){
        _login.update { it.copy(email = value, emailError =validateEmail(value)) }
        //recalcular si el boton debe habilitarse
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String){
        _login.update { it.copy(pass = value) }
        recomputeLoginCanSubmit()
    }

    fun submitLogin(){
        val s = _login.value
        if(!s.canSubmit || s.isSubmitting) return
        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }
            delay(1000) //simulacion de tiempo de verificado
        }
        //buscamos si el email existe en algun usuario
        val user = USERS.firstOrNull{ it.email.equals(s.email, ignoreCase = true)}
        //coincide el correo verificamos ahora con la clave
        val ok = user !=null && user.pass == s.pass
        //actualizamos con el resultado
        _login.update {
            it.copy(
                isSubmitting = false,
                success = ok,
                errorMsg = if(!ok) "Correo y/o clave incorrectas" else null
            )
        }
    }

    // Limpia banderas tras navegar
    fun clearLoginResult(){
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- REGISTRO: handlers y envío -----------------

    fun onNameChange(value: String) {                       // Handler del nombre
        val filtered = value.filter { it.isLetter() || it.isWhitespace() } // Filtramos números/símbolos (solo letras/espacios)
        _register.update {                                  // Guardamos + validamos
            it.copy(name = filtered, nameError = validateNameLettersOnly(filtered))
        }
        recomputeRegisterCanSubmit()                        // Recalculamos habilitado
    }

    fun onRegisterEmailChange(value: String) {              // Handler del email
        _register.update { it.copy(email = value, emailError = validateEmail(value)) } // Guardamos + validamos
        recomputeRegisterCanSubmit()
    }

    fun onPhoneChange(value: String) {                      // Handler del teléfono
        val digitsOnly = value.filter { it.isDigit() }      // Dejamos solo dígitos
        _register.update {                                  // Guardamos + validamos
            it.copy(phone = digitsOnly, phoneError = validatePhoneDigitsOnly(digitsOnly))
        }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {               // Handler de la contraseña
        _register.update { it.copy(pass = value, passError = validateStrongPassword(value)) } // Validamos seguridad
        // Revalidamos confirmación con la nueva contraseña
        _register.update { it.copy(confirmError = validateConfirm(it.pass, it.confirm)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {                    // Handler de confirmación
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.pass, value)) } // Guardamos + validamos
        recomputeRegisterCanSubmit()
    }

    private fun recomputeRegisterCanSubmit() {              // Habilitar "Registrar" si todo OK
        val s = _register.value                              // Tomamos el estado actual
        val noErrors = listOf(s.nameError, s.emailError, s.phoneError, s.passError, s.confirmError).all { it == null } // Sin errores
        val filled = s.name.isNotBlank() && s.email.isNotBlank() && s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank() // Todo lleno
        _register.update { it.copy(canSubmit = noErrors && filled) } // Actualizamos flag
    }

    fun submitRegister() {                                  // Acción de registro (simulación async)
        val s = _register.value                              // Snapshot del estado
        if (!s.canSubmit || s.isSubmitting) return          // Evitamos reentradas
        viewModelScope.launch {                             // Corrutina
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) } // Loading
            delay(700)                                      // Simulamos IO

            // ¿Existe ya un usuario con el mismo email en la **colección**?
            val duplicated = USERS.any { it.email.equals(s.email, ignoreCase = true) }

            if (duplicated) {                               // Si ya existe, devolvemos error
                _register.update {
                    it.copy(isSubmitting = false, success = false, errorMsg = "El usuario ya existe")
                }
                return@launch                                // Salimos
            }

            // Insertamos el nuevo usuario en la **colección** (solo demo; no persistimos)
            USERS.add(
                DemoUser(
                    name = s.name.trim(),
                    email = s.email.trim(),
                    phone = s.phone.trim(),
                    pass = s.pass                            // En demo lo guardamos en texto (para clase)
                )
            )

            _register.update {                               // Éxito
                it.copy(isSubmitting = false, success = true, errorMsg = null)
            }
        }
    }

    fun clearRegisterResult() {                             // Limpia banderas tras navegar
        _register.update { it.copy(success = false, errorMsg = null) }
    }

}
