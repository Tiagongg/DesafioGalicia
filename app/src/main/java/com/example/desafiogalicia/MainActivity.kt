package com.example.desafiogalicia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.desafiogalicia.data.model.User
import com.example.desafiogalicia.navigation.AppNavigation
import com.example.desafiogalicia.ui.theme.DesafioGaliciaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DesafioGaliciaTheme {
                val navController = rememberNavController()
                val userDetailCache = remember { mutableMapOf<String, User>() }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        userDetailCache = userDetailCache
                    )
                }
            }
        }
    }
}

