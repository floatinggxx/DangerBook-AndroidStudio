package com.example.DangerBook
import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.DangerBook.data.local.database.AppDatabase
import com.example.DangerBook.data.local.notifications.NotificationHelper
import com.example.DangerBook.data.local.storage.UserPreferences
import com.example.DangerBook.data.repository.UsuarioRepository
import com.example.DangerBook.data.repository.ServicioRepository
import com.example.DangerBook.data.repository.CitaRepository
import com.example.DangerBook.data.repository.HorarioRepository
import com.example.DangerBook.data.repository.DisponibilidadRepository
import com.example.DangerBook.data.repository.BloqueRepository
import com.example.DangerBook.data.repository.DiaRepository
import com.example.DangerBook.navigation.AppNavGraph
import com.example.DangerBook.ui.viewmodel.AuthViewModel
import com.example.DangerBook.ui.viewmodel.AuthViewModelFactory
import com.example.DangerBook.ui.viewmodel.ServicesViewModel
import com.example.DangerBook.ui.viewmodel.ServiciosViewModelFactory
import com.example.DangerBook.ui.viewmodel.AppointmentViewModel
import com.example.DangerBook.ui.viewmodel.CitaViewModelFactory
import com.example.DangerBook.ui.theme.UINavegacionTheme
import com.example.DangerBook.ui.viewmodel.AdminViewModel
import com.example.DangerBook.ui.viewmodel.AdminViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Launcher para solicitar permiso de notificaciones
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {

        } else {
            // Permiso denegado (la app seguirá funcionando sin notificaciones)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Crear canal de notificaciones
        NotificationHelper.createNotificationChannel(this)

        // Solicitar permiso de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            UINavegacionTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current.applicationContext
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // DataStore para persistencia de sesión
    val userPrefs = remember { UserPreferences(context) }

    // Inicializar base de datos y DAOs
    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()
    val serviceDao = db.serviceDao()
    val barberDao = db.barberDao()
    val appointmentDao = db.appointmentDao()

    // Inicializar repositorios
    val usuarioRepository = UsuarioRepository(userDao)
    val servicioRepository = ServicioRepository(serviceDao, barberDao)
    val horarioRepository = HorarioRepository()
    val disponibilidadRepository = DisponibilidadRepository()
    val bloqueRepository = BloqueRepository()
    val diaRepository = DiaRepository()
    val citaRepository = CitaRepository(
        appointmentDao,
        userDao,
        serviceDao,
        barberDao,
        horarioRepository,
        disponibilidadRepository,
        bloqueRepository,
        diaRepository
    )

    // Estado de autenticación desde DataStore
    val currentUserId by userPrefs.userId.collectAsStateWithLifecycle(null)
    val currentUserName by userPrefs.userName.collectAsStateWithLifecycle(null)
    val currentUserEmail by userPrefs.userEmail.collectAsStateWithLifecycle(null)
    val currentUserPhone by userPrefs.userPhone.collectAsStateWithLifecycle(null)
    val currentUserRole by userPrefs.userRole.collectAsStateWithLifecycle(null)

    // Crear AuthViewModel
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(usuarioRepository)
    )

    val adminViewModel: AdminViewModel = viewModel(
        factory = AdminViewModelFactory(usuarioRepository, citaRepository)
    )

    // Observar el estado de login para guardar sesión en DataStore
    LaunchedEffect(authViewModel) {
        authViewModel.login.collectLatest { loginState ->
            if (loginState.success && loginState.loggedUser != null) {
                val user = loginState.loggedUser
                userPrefs.saveUserSession(
                    userId = user.id,
                    userName = user.name,
                    userEmail = user.email,
                    userPhone = user.phone,
                    userRole = user.role,
                    userPhoto = user.photoUri
                )
            }
        }
    }

    // Crear ServicesViewModel
    val servicesViewModel: ServicesViewModel = viewModel(
        factory = ServiciosViewModelFactory(servicioRepository)
    )

    // Crear AppointmentViewModel con la nueva factory
    val appointmentViewModel: AppointmentViewModel = viewModel(
        factory = CitaViewModelFactory(
            repository = citaRepository,
            userPreferences = userPrefs // Le pasamos el gestor de sesión
        )
    )

    // Cerrar sesión
    val handleLogout: () -> Unit = {
        scope.launch {
            userPrefs.clearSession()
        }
    }

    // Actualizar foto de perfil
    val handlePhotoUpdated: (String) -> Unit = { photoUri ->
        scope.launch {
            currentUserId?.let { userId ->
                usuarioRepository.updateUserPhoto(userId, photoUri)
                userPrefs.updateUserPhoto(photoUri)
            }
        }
    }

    // Actualizar nombre de usuario
    val handleUserNameUpdated: (String) -> Unit = { newName ->
        scope.launch {
            currentUserId?.let { userId ->
                authViewModel.updateUserName(userId, newName)
                userPrefs.updateUserName(newName)
            }
        }
    }

    // Actualizar email de usuario
    val handleUserEmailUpdated: (String) -> Unit = { newEmail ->
        scope.launch {
            currentUserId?.let { userId ->
                authViewModel.updateUserEmail(userId, newEmail)
                userPrefs.updateUserEmail(newEmail)
            }
        }
    }

    // Actualizar teléfono de usuario
    val handleUserPhoneUpdated: (String) -> Unit = { newPhone ->
        scope.launch {
            currentUserId?.let { userId ->
                authViewModel.updateUserPhone(userId, newPhone)
                userPrefs.updateUserPhone(newPhone)
            }
        }
    }

    // Actualizar contraseña de usuario
    val handleUserPasswordUpdated: (String) -> Unit = { newPassword ->
        scope.launch {
            currentUserId?.let { userId ->
                authViewModel.updateUserPassword(userId, newPassword)
            }
        }
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        AppNavGraph(
            navController = navController,
            authViewModel = authViewModel,
            servicesViewModel = servicesViewModel,
            appointmentViewModel = appointmentViewModel,
            adminViewModel = adminViewModel, 
            currentUserId = currentUserId,
            currentUserName = currentUserName,
            currentUserEmail = currentUserEmail,
            currentUserPhone = currentUserPhone,
            currentUserRole = currentUserRole,
            onLogout = handleLogout,
            onPhotoUpdated = handlePhotoUpdated,
            onUserNameUpdated = handleUserNameUpdated,
            onUserEmailUpdated = handleUserEmailUpdated,
            onUserPhoneUpdated = handleUserPhoneUpdated,
            onUserPasswordUpdated = handleUserPasswordUpdated
        )
    }
}