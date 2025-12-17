package com.jakobneukirchner.perdis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jakobneukirchner.perdis.ui.DienstplanScreen
import com.jakobneukirchner.perdis.ui.LoginScreen
import com.jakobneukirchner.perdis.ui.theme.PerdisTheme
import com.jakobneukirchner.perdis.viewmodel.DienstplanViewModel
import com.jakobneukirchner.perdis.viewmodel.LoginViewModel
import com.jakobneukirchner.perdis.viewmodel.PerdisViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PerdisTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val factory = PerdisViewModelFactory(applicationContext)

                    val loginViewModel: LoginViewModel = viewModel(factory = factory)
                    val dienstplanViewModel: DienstplanViewModel = viewModel(factory = factory)

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = {
                                    navController.navigate("dienstplan") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("dienstplan") {
                            DienstplanScreen(
                                viewModel = dienstplanViewModel,
                                onLogout = {
                                    loginViewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo("dienstplan") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
