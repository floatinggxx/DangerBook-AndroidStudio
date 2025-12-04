package com.example.DangerBook.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.DangerBook.ui.components.AppDrawer
import com.example.DangerBook.ui.components.AppTopBar
import com.example.DangerBook.ui.components.authenticatedDrawerItems
import com.example.DangerBook.ui.components.defaultDrawerItems
import com.example.DangerBook.ui.screen.AdminDashboardScreen
import com.example.DangerBook.ui.screen.BarberAppointmentsScreen
import com.example.DangerBook.ui.screen.BookAppointmentScreen
import com.example.DangerBook.ui.screen.ForgotPasswordScreen
import com.example.DangerBook.ui.screen.HomeScreen
import com.example.DangerBook.ui.screen.LoginScreenVm
import com.example.DangerBook.ui.screen.MyAppointmentsScreen
import com.example.DangerBook.ui.screen.ProfileScreen
import com.example.DangerBook.ui.screen.RegisterScreenVm
import com.example.DangerBook.ui.screen.ResenasScreen
import com.example.DangerBook.ui.screen.ServicesScreen
import com.example.DangerBook.ui.viewmodel.AdminViewModel
import com.example.DangerBook.ui.viewmodel.AppointmentViewModel
import com.example.DangerBook.ui.viewmodel.AuthViewModel
import com.example.DangerBook.ui.viewmodel.ResenaViewModel
import com.example.DangerBook.ui.viewmodel.ServicesViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    servicesViewModel: ServicesViewModel,
    appointmentViewModel: AppointmentViewModel,
    adminViewModel: AdminViewModel,
    resenaViewModel: ResenaViewModel, // ViewModel de reseñas
    currentUserId: Long?,
    currentUserName: String?,
    currentUserEmail: String?,
    currentUserPhone: String?,
    currentUserRole: String?,
    onLogout: () -> Unit,
    onPhotoUpdated: (ByteArray) -> Unit,
    onUserNameUpdated: (String) -> Unit,
    onUserEmailUpdated: (String) -> Unit,
    onUserPhoneUpdated: (String) -> Unit,
    onUserPasswordUpdated: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isAuthenticated = currentUserId != null

    val goHome: () -> Unit = { navController.navigate("home") }
    val goLogin: () -> Unit = { navController.navigate("login") }
    val goRegister: () -> Unit = { navController.navigate("register") }
    val goForgotPassword: () -> Unit = { navController.navigate("forgot_password") }
    val goServices: () -> Unit = { navController.navigate("services") }
    val goBookAppointment: () -> Unit = { navController.navigate("book_appointment") }
    val goMyAppointments: () -> Unit = { navController.navigate("my_appointments") }
    val goProfile: () -> Unit = { navController.navigate("profile") }
    val goReviews: () -> Unit = { navController.navigate("reviews") } // Navegación a reseñas
    val goAdminDashboard: () -> Unit = { navController.navigate("admin_dashboard") }
    val goBarberAppointments: () -> Unit = { navController.navigate("barber_appointments") }

    val handleLogout: () -> Unit = {
        scope.launch { drawerState.close() }
        onLogout()
        navController.navigate("home") {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = navController.currentBackStackEntry?.destination?.route,
                items = if (isAuthenticated) {
                    authenticatedDrawerItems(
                        userName = currentUserName ?: "Usuario",
                        onHome = {
                            scope.launch { drawerState.close() }
                            goHome()
                        },
                        onServices = {
                            scope.launch { drawerState.close() }
                            goServices()
                        },
                        onBookAppointment = {
                            scope.launch { drawerState.close() }
                            goBookAppointment()
                        },
                        onMyAppointments = {
                            scope.launch { drawerState.close() }
                            goMyAppointments()
                        },
                        onProfile = {
                            scope.launch { drawerState.close() }
                            goProfile()
                        },
                        onReviews = { // Añadido al drawer
                            scope.launch { drawerState.close() }
                            goReviews()
                        },
                        onLogout = handleLogout,
                        userRole = currentUserRole ?: "",
                        onBarberAppointments = {
                            scope.launch { drawerState.close() }
                            goBarberAppointments()
                        },
                        onAdminDashboard = {
                            scope.launch { drawerState.close() }
                            goAdminDashboard()
                        }
                    )
                } else {
                    defaultDrawerItems(
                        onHome = {
                            scope.launch { drawerState.close() }
                            goHome()
                        },
                        onLogin = {
                            scope.launch { drawerState.close() }
                            goLogin()
                        },
                        onRegister = {
                            scope.launch { drawerState.close() }
                            goRegister()
                        },
                        onReviews = { // Añadido al drawer
                            scope.launch { drawerState.close() }
                            goReviews()
                        }
                    )
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    isAuthenticated = isAuthenticated,
                    userName = currentUserName,
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onHome = goHome,
                    onLogin = goLogin,
                    onRegister = goRegister,
                    onServices = goServices,
                    onBookAppointment = goBookAppointment,
                    onMyAppointments = goMyAppointments,
                    onProfile = goProfile,
                    onLogout = handleLogout,
                    userRole = currentUserRole ?: ""
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {

                composable("home") {
                    HomeScreen(
                        isAuthenticated = isAuthenticated,
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onGoServices = goServices,
                        onGoReviews = goReviews, // Pasar la función de navegación
                        userRole = currentUserRole ?: ""
                    )
                }

                composable("login") {
                    DisposableEffect(Unit) {
                        onDispose {
                            authViewModel.clearLoginForm()
                        }
                    }
                    LoginScreenVm(
                        vm = authViewModel,
                        onLoginOkNavigateHome = {
                            navController.navigate("services") {
                                popUpTo("home") { inclusive = false }
                            }
                        },
                        onGoRegister = goRegister,
                        onGoForgotPassword = goForgotPassword
                    )
                }

                composable("register") {
                    DisposableEffect(Unit) {
                        onDispose {
                            authViewModel.clearRegisterForm()
                        }
                    }
                    RegisterScreenVm(
                        vm = authViewModel,
                        onRegisteredNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }
                
                composable("forgot_password") {
                    ForgotPasswordScreen(
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }

                composable("services") {
                    if (!isAuthenticated) {
                        navController.navigate("login")
                    } else {
                        ServicesScreen(
                            vm = servicesViewModel,
                            onBookService = { 
                                goBookAppointment()
                            }
                        )
                    }
                }

                composable("book_appointment") {
                    if (!isAuthenticated) {
                        navController.navigate("login")
                    } else {
                        BookAppointmentScreen(
                            appointmentVm = appointmentViewModel,
                            servicesVm = servicesViewModel,
                            onAppointmentBooked = {
                                navController.navigate("home") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }

                composable("my_appointments") {
                    if (!isAuthenticated) {
                        navController.navigate("login")
                    } else {
                        MyAppointmentsScreen(
                            vm = appointmentViewModel,
                            onBookNewAppointment = goBookAppointment
                        )
                    }
                }

                composable("profile") {
                    if (!isAuthenticated) {
                        navController.navigate("login")
                    } else {
                        ProfileScreen(
                            userId = currentUserId!!,
                            userName = currentUserName ?: "Usuario",
                            userEmail = currentUserEmail ?: "",
                            userPhone = currentUserPhone ?: "",
                            userRole = currentUserRole ?: "",
                            onLogout = handleLogout,
                            onPhotoUpdated = onPhotoUpdated,
                            onUserNameUpdated = onUserNameUpdated,
                            onUserEmailUpdated = onUserEmailUpdated,
                            onUserPhoneUpdated = onUserPhoneUpdated,
                            onUserPasswordUpdated = onUserPasswordUpdated
                        )
                    }
                }
                
                 composable("reviews") { // Nueva pantalla de reseñas
                    if (!isAuthenticated) {
                        navController.navigate("login")
                    } else {
                        ResenasScreen(
                            vm = resenaViewModel,
                            isAdmin = currentUserRole == "admin"
                        )
                    }
                }

                composable("admin_dashboard") {
                    if (currentUserRole != "admin") {
                        navController.navigate("home") { popUpTo("home") { inclusive = true } }                    } else {
                        val adminState by adminViewModel.uiState.collectAsState()

                        if (adminState.isLoading) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        } else {
                            AdminDashboardScreen(
                                totalAppointments = adminState.totalAppointments,
                                totalUsers = adminState.totalUsers,
                                totalBarbers = adminState.totalBarbers,
                                onManageUsers = {},
                                onManageServices = {},
                                onManageResenas = goReviews,
                                onViewReports = {}
                            )
                        }
                    }
                }

                composable("barber_appointments") {
                    if (currentUserRole != "barber" || currentUserId == null) {
                        navController.navigate("home") { popUpTo("home") { inclusive = true } }                    } else {
                        LaunchedEffect(Unit) {
                            appointmentViewModel.loadBarberAppointments(currentUserId)
                        }

                        val barberState by appointmentViewModel.myAppointmentsState.collectAsState()

                        if (barberState.isLoading) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        } else {
                            BarberAppointmentsScreen(
                                appointments = barberState.allAppointments,
                                onConfirmAppointment = { appointmentId ->
                                    appointmentViewModel.confirmAppointment(appointmentId)
                                },
                                onCompleteAppointment = { appointmentId ->
                                    appointmentViewModel.completeAppointment(appointmentId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
