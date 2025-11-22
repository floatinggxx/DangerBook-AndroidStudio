package com.example.DangerBook.ui.screen

import androidx.compose.foundation.background                 // Fondo
import androidx.compose.foundation.layout.*                   // Box/Column/Row/Spacer
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons                  // Íconos Material
import androidx.compose.material.icons.filled.Visibility      // Ícono mostrar
import androidx.compose.material.icons.filled.VisibilityOff   // Ícono ocultar
import androidx.compose.material3.*                           // Material 3
import androidx.compose.runtime.*                             // remember, Composable
import androidx.compose.ui.Alignment                          // Alineaciones
import androidx.compose.ui.Modifier                           // Modificador
import androidx.compose.ui.text.input.*                       // KeyboardOptions/Types/Transformations
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp                            // DPs
import androidx.lifecycle.compose.collectAsStateWithLifecycle // Observa StateFlow
import com.example.DangerBook.ui.viewmodel.AuthViewModel         // ViewModel

//1 creamos la union con el viewmodel creado
@Composable // Pantalla Registro conectada al VM
fun RegisterScreenVm(
    vm: AuthViewModel,
    onRegisteredNavigateLogin: () -> Unit, // Navega a Login si success=true
    onGoLogin: () -> Unit, // Botón alternativo para ir a Login
    allowRoleSelection: Boolean = false // NUEVO: permitir seleccionar rol (solo para admin)
) {
    val state by vm.register.collectAsStateWithLifecycle() // Observa estado en tiempo real

    if (state.success) { // Si registro fue exitoso
        vm.clearRegisterResult() // Limpia banderas
        onRegisteredNavigateLogin() // Navega a Login
    }

    RegisterScreen( // Delegamos UI presentacional
        name = state.name,
        apellido = state.apellido, // NUEVO
        email = state.email,
        phone = state.phone,
        pass = state.pass,
        confirm = state.confirm,
        role = state.role,
        nameError = state.nameError,
        apellidoError = state.apellidoError, // NUEVO
        emailError = state.emailError,
        phoneError = state.phoneError,
        passError = state.passError,
        confirmError = state.confirmError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,
        allowRoleSelection = allowRoleSelection,
        onNameChange = vm::onNameChange,
        onApellidoChange = vm::onApellidoChange, // NUEVO
        onEmailChange = vm::onRegisterEmailChange,
        onPhoneChange = vm::onPhoneChange,
        onPassChange = vm::onRegisterPassChange,
        onConfirmChange = vm::onConfirmChange,
        onSubmit = vm::submitRegister,
        onGoLogin = onGoLogin
    )
}


//2 ajustamos el private y parametros
@Composable // Pantalla Registro (solo navegación)
private fun RegisterScreen(
    name: String,
    apellido: String, // NUEVO
    email: String,
    phone: String,
    pass: String,
    confirm: String,
    nameError: String?,
    apellidoError: String?, // NUEVO
    emailError: String?,
    phoneError: String?,
    passError: String?,
    confirmError: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,
    onNameChange: (String) -> Unit,
    onApellidoChange: (String) -> Unit, // NUEVO
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoLogin: () -> Unit,
    allowRoleSelection: Boolean,
    role: String
) {
    val bg = MaterialTheme.colorScheme.secondaryContainer // Change background color
    var showPass by remember { mutableStateOf(false) }        // Mostrar/ocultar password
    var showConfirm by remember { mutableStateOf(false) }     // Mostrar/ocultar confirm

    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo
            .background(bg) // Fondo
            .padding(24.dp), // Aumentar padding
        contentAlignment = Alignment.Center // Centro
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), // Estructura vertical
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crea tu Cuenta",
                style = MaterialTheme.typography.headlineMedium, // Título más grande
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.height(8.dp)) // Separación

            Text(
                text = "Completa tus datos para empezar.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.height(24.dp))

            // ---------- NOMBRE Y APELLIDO EN UNA FILA ----------
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // --- Nombre ---
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Nombre") },
                    singleLine = true,
                    isError = nameError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.weight(1f)
                )
                // --- Apellido ---
                OutlinedTextField(
                    value = apellido,
                    onValueChange = onApellidoChange,
                    label = { Text("Apellido") },
                    singleLine = true,
                    isError = apellidoError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.weight(1f)
                )
            }
            if (nameError != null) {
                Text(nameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            } else if (apellidoError != null) {
                Text(apellidoError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp))

            // ---------- EMAIL ----------
            OutlinedTextField(
                value = email,                               // Valor actual
                onValueChange = onEmailChange,               // Notifica VM (valida)
                label = { Text("Correo electrónico") },                   // Etiqueta
                singleLine = true,                           // Una línea
                isError = emailError != null,                // Marca error
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email        // Teclado de email
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError != null) {                        // Muestra error
                Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp))                    // Espacio

            // ---------- TELÉFONO (solo números). El VM ya filtra a dígitos ----------
            OutlinedTextField(
                value = phone,                               // Valor actual (solo dígitos)
                onValueChange = onPhoneChange,               // Notifica VM (filtra y valida)
                label = { Text("Teléfono") },                // Etiqueta
                singleLine = true,                           // Una línea
                isError = phoneError != null,                // Marca error
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number       // Teclado numérico
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (phoneError != null) {                        // Muestra error
                Text(phoneError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp))                    // Espacio

            // ---------- PASSWORD (segura) ----------
            OutlinedTextField(
                value = pass,                                // Valor actual
                onValueChange = onPassChange,                // Notifica VM (valida fuerza)
                label = { Text("Contraseña") },              // Etiqueta
                singleLine = true,                           // Una línea
                isError = passError != null,                 // Marca error
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(), // Oculta/mostrar
                trailingIcon = {                             // Icono para alternar visibilidad
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (passError != null) {                         // Muestra error
                Text(passError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(16.dp))                    // Espacio

            // ---------- CONFIRMAR PASSWORD ----------
            OutlinedTextField(
                value = confirm,                             // Valor actual
                onValueChange = onConfirmChange,             // Notifica VM (valida igualdad)
                label = { Text("Confirmar contraseña") },    // Etiqueta
                singleLine = true,                           // Una línea
                isError = confirmError != null,              // Marca error
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(), // Oculta/mostrar
                trailingIcon = {                             // Icono para alternar visibilidad
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(
                            imageVector = if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showConfirm) "Ocultar confirmación" else "Mostrar confirmación"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (confirmError != null) {                      // Muestra error
                Text(confirmError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(24.dp))                   // Espacio

            // ---------- BOTÓN REGISTRAR ----------
            Button(
                onClick = onSubmit,                          // Intenta registrar (inserta en la colección)
                enabled = canSubmit && !isSubmitting,        // Solo si todo es válido y no cargando
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isSubmitting) {                          // Muestra loading mientras “procesa”
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(12.dp))
                    Text("Creando cuenta...")
                } else {
                    Text("Registrarme")
                }
            }

            if (errorMsg != null) {                          // Error global (ej: usuario duplicado)
                Spacer(Modifier.height(8.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))                   // Espacio

            // ---------- BOTÓN IR A LOGIN ----------
            TextButton(onClick = onGoLogin) {
                Text("¿Ya tienes una cuenta? Inicia sesión")
            }
        }
    }
}