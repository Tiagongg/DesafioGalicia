package com.example.desafiogalicia.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.desafiogalicia.data.model.User
import com.example.desafiogalicia.presentation.screens.userdetail.UserDetailScreen
import com.example.desafiogalicia.presentation.screens.userlist.UserListScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    userDetailCache: MutableMap<String, User> = mutableMapOf()
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.USER_LIST
    ) {
        composable(AppDestinations.USER_LIST) {
            UserListScreen(
                onUserClick = { user ->
                    userDetailCache[user.login.uuid] = user
                    navController.navigate("${AppDestinations.USER_DETAIL}/${user.login.uuid}")
                }
            )
        }
        
        composable("${AppDestinations.USER_DETAIL}/{userUuid}") { backStackEntry ->
            val userUuid = backStackEntry.arguments?.getString("userUuid") ?: ""
            val user = userDetailCache[userUuid]
            
            if (user != null) {
                UserDetailScreen(
                    user = user,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

object AppDestinations {
    const val USER_LIST = "user_list"
    const val USER_DETAIL = "user_detail"
}