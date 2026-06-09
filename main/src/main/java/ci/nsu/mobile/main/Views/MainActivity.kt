@file:OptIn(ExperimentalMaterial3Api::class)

package ci.nsu.mobile.main.Views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ci.nsu.mobile.main.MyApp
import ci.nsu.mobile.main.ViewModels.NewCalculationViewModel
import ci.nsu.mobile.main.ViewModels.UserListViewModel
import ci.nsu.mobile.main.ui.theme.PracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticeTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val items = listOf(
        Screen.Users to "Пользователи",
        Screen.MyCalculations to "Мои расчёты",
        Screen.NewCalculation to "Новый расчёт"
    )
    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Приложение") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            // Используем NavigationBar вместо BottomNavigation
            NavigationBar {
                items.forEach { (screen, title) ->
                    // Используем NavigationBarItem вместо BottomNavigationItem
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                when (screen) {
                                    Screen.Users -> Icons.Default.People
                                    Screen.MyCalculations -> Icons.Default.History
                                    Screen.NewCalculation -> Icons.Default.Receipt
                                },
                                contentDescription = title
                            )
                        },
                        label = { Text(title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Users.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Users.route) {
                val serviceLocator = MyApp.serviceLocator
                val viewModel = UserListViewModel(serviceLocator.authRepository)
                UserListScreen(
                    viewModel = viewModel,
                    onLogout = {
                        // Выход из системы
                        serviceLocator.tokenManager.clear()
                        serviceLocator.userPreferences.clear()
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        (context as? Activity)?.finish()
                    }
                )
            }
            composable(Screen.MyCalculations.route) {
                MyCalculationsScreen()
            }
            composable(Screen.NewCalculation.route) {
                val serviceLocator = MyApp.serviceLocator
                val viewModel: NewCalculationViewModel = viewModel(
                    factory = NewCalculationViewModel.provideFactory(
                        serviceLocator.calculationRepository,
                        serviceLocator.userPreferences
                    )
                )
                NewCalculationScreen(viewModel = viewModel)
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Users : Screen("users")
    object MyCalculations : Screen("my_calculations")
    object NewCalculation : Screen("new_calculation")
}