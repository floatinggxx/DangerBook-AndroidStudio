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
import com.example.DangerBook.data.repository.*
import com.example.DangerBook.navigation.AppNavGraph
import com.example.DangerBook.ui.viewmodel.*
import com.example.DangerBook.ui.theme.UINavegacionTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // CORRECCIÓN: Volver a añadir el launcher de permisos que fue borrado por error
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // El permiso fue concedido
        } else {
            // El permiso fue denegado
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this)

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

    val userPrefs = remember { UserPreferences(context) }

    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()
    val serviceDao = db.serviceDao()
    val barberDao = db.barberDao()
    val appointmentDao = db.appointmentDao()

    // Repositorios
    val usuarioRepository = UsuarioRepository(userDao)
    val servicioRepository = ServicioRepository(serviceDao, barberDao)
    val disponibilidadRepository = DisponibilidadRepository()
    val citaRepository = CitaRepository(
        appointmentDao,
        userDao,
        serviceDao,
        barberDao,
        disponibilidadRepository
    )
    val resenaRepository = ResenaRepository()

    // ViewModels
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(usuarioRepository))
    val adminViewModel: AdminViewModel = viewModel(factory = AdminViewModelFactory(usuarioRepository, citaRepository))
    val servicesViewModel: ServicesViewModel = viewModel(factory = ServiciosViewModelFactory(servicioRepository))
    val appointmentViewModel: AppointmentViewModel = viewModel(factory = CitaViewModelFactory(repository = citaRepository, userPreferences = userPrefs))
    val resenaViewModel: ResenaViewModel = viewModel(factory = ResenaViewModelFactory(resenaRepository))

    val currentUserId by userPrefs.userId.collectAsStateWithLifecycle(null)
    val currentUserName by userPrefs.userName.collectAsStateWithLifecycle(null)
    val currentUserEmail by userPrefs.userEmail.collectAsStateWithLifecycle(null)
    val currentUserPhone by userPrefs.userPhone.collectAsStateWithLifecycle(null)
    val currentUserRole by userPrefs.userRole.collectAsStateWithLifecycle(null)

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

    val handleLogout: () -> Unit = {
        scope.launch {
            userPrefs.clearSession()
        }
    }

    val handlePhotoUpdated: (String) -> Unit = { photoUri ->
        scope.launch {
            currentUserId?.let { userId ->
                usuarioRepository.updateUserPhoto(userId, photoUri)
                userPrefs.updateUserPhoto(photoUri)
            }
        }
    }

    val handleUserNameUpdated: (String) -> Unit = { newName ->
        scope.launch {
            currentUserId?.let { userId ->
                authViewModel.updateUserName(userId, newName)
                userPrefs.updateUserName(newName)
            }
        }
    }

    val handleUserEmailUpdated: (String) -> Unit = { newEmail ->
        scope.launch {
            currentUserId?.let { userId ->
                authViewModel.updateUserEmail(userId, newEmail)
                userPrefs.updateUserEmail(newEmail)
            }
        }
    }

    val handleUserPhoneUpdated: (String) -> Unit = { newPhone ->
        scope.launch {
            currentUserId?.let { userId ->
                authViewModel.updateUserPhone(userId, newPhone)
                userPrefs.updateUserPhone(newPhone)
            }
        }
    }

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
            resenaViewModel = resenaViewModel,
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