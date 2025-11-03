package com.example.DangerBook.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.DangerBook.ui.screen.AdminDashboardScreen
import kotlinx.coroutines.launch
import com.example.DangerBook.ui.components.AppTopBar
import com.example.DangerBook.ui.components.AppDrawer
import com.example.DangerBook.ui.components.authenticatedDrawerItems
import com.example.DangerBook.ui.components.defaultDrawerItems
import com.example.DangerBook.ui.screen.* 
import com.example.DangerBook.ui.viewmodel.AdminViewModel
import com.example.DangerBook.ui.viewmodel.AuthViewModel
import com.example.DangerBook.ui.viewmodel.ServicesViewModel
import com.example.DangerBook.ui.viewmodel.AppointmentViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    servicesViewModel: ServicesViewModel,
    appointmentViewModel: AppointmentViewModel,
    adminViewModel: AdminViewModel,
    currentUserId: Long?,
    currentUserName: String?,
    currentUserRole: String?,
    onLogout: () -> Unit,
    onPhotoUpdated: (String) -> Unit,
    onUserNameUpdated: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isAuthenticated = currentUserId != null

    val goHome: () -> Unit = { navController.navigate(Route.Home.path) }
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) }
    val goServices: () -> Unit = { navController.navigate(Route.Services.path) }
    val goBookAppointment: () -> Unit = { navController.navigate(Route.BookAppointment.path) }
    val goMyAppointments: () -> Unit = { navController.navigate(Route.MyAppointments.path) }
    val goProfile: () -> Unit = { navController.navigate(Route.Profile.path) }
    val goAdminDashboard: () -> Unit = { navController.navigate(Route.AdminDashboard.path) }
    val goBarberAppointments: () -> Unit = { navController.navigate(Route.BarberAppointments.path) }

    val handleLogout: () -> Unit = {
        scope.launch { drawerState.close() }
        onLogout()
        navController.navigate(Route.Home.path) {
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
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {

                composable(Route.Home.path) {
                    HomeScreen(
                        isAuthenticated = isAuthenticated,
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onGoServices = goServices,
                        userRole = currentUserRole ?: ""
                    )
                }

                composable(Route.Login.path) {
                    LoginScreenVm(
                        vm = authViewModel,
                        onLoginOkNavigateHome = {
                            navController.navigate(Route.Services.path) {
                                popUpTo(Route.Home.path) { inclusive = false }
                            }
                        },
                        onGoRegister = goRegister
                    )
                }

                composable(Route.Register.path) {
                    RegisterScreenVm(
                        vm = authViewModel,
                        onRegisteredNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }

                composable(Route.Services.path) {
                    if (!isAuthenticated) {
                        navController.navigate(Route.Login.path)
                    } else {
                        ServicesScreen(
                            vm = servicesViewModel,
                            onBookService = { 
                                goBookAppointment()
                            }
                        )
                    }
                }

                composable(Route.BookAppointment.path) {
                    if (!isAuthenticated) {
                        navController.navigate(Route.Login.path)
                    } else {
                        BookAppointmentScreen(
                            appointmentVm = appointmentViewModel,
                            servicesVm = servicesViewModel,
                            onAppointmentBooked = {
                                navController.navigate(Route.Home.path) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }

                composable(Route.MyAppointments.path) {
                    if (!isAuthenticated) {
                        navController.navigate(Route.Login.path)
                    } else {
                        MyAppointmentsScreen(
                            vm = appointmentViewModel,
                            onBookNewAppointment = goBookAppointment
                        )
                    }
                }

                composable(Route.Profile.path) {
                    if (!isAuthenticated) {
                        navController.navigate(Route.Login.path)
                    } else {
                        ProfileScreen(
                            userId = currentUserId!!,
                            userName = currentUserName ?: "Usuario",
                            onLogout = handleLogout,
                            onPhotoUpdated = onPhotoUpdated,
                            userRole = currentUserRole ?: "",
                            onUserNameUpdated = onUserNameUpdated
                        )
                    }
                }

                composable(Route.AdminDashboard.path) {
                    if (currentUserRole != "admin") {
                        navController.navigate(Route.Home.path) { popUpTo(Route.Home.path) { inclusive = true } }
                    } else {
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
                                onViewReports = {}
                            )
                        }
                    }
                }

                composable(Route.BarberAppointments.path) {
                    if (currentUserRole != "barber" || currentUserId == null) {
                        navController.navigate(Route.Home.path) { popUpTo(Route.Home.path) { inclusive = true } }
                    } else {
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
