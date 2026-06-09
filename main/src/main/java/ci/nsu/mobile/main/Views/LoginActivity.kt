package ci.nsu.mobile.main.Views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import ci.nsu.mobile.main.MyApp
import ci.nsu.mobile.main.ViewModels.LoginViewModel
import ci.nsu.mobile.main.ViewModels.RegistrationViewModel
import ci.nsu.mobile.main.ui.theme.PracticeTheme

class LoginActivity : ComponentActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Получаем зависимости из ServiceLocator
        val serviceLocator = MyApp.serviceLocator
        val authRepository = serviceLocator.authRepository

        // Создаём ViewModel для входа
        loginViewModel = ViewModelProvider(
            this,
            LoginViewModel.provideFactory(authRepository)
        )[LoginViewModel::class.java]

        // ViewModel для регистрации
        registrationViewModel = RegistrationViewModel(authRepository)

        setContent {
            PracticeTheme {
                LoginNavigator(
                    loginViewModel = loginViewModel,
                    registrationViewModel = registrationViewModel,
                    onLoginSuccess = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun LoginNavigator(
    loginViewModel: LoginViewModel,
    registrationViewModel: RegistrationViewModel,
    onLoginSuccess: () -> Unit
) {
    var showRegistration by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        if (showRegistration) {
            RegistrationScreen(
                viewModel = registrationViewModel,
                onRegisterSuccess = { showRegistration = false },
                onNavigateBack = { showRegistration = false },
                modifier = Modifier
            )
        } else {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = onLoginSuccess,
                onNavigateToRegister = { showRegistration = true },
                modifier = Modifier
            )
        }
    }
}