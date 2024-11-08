package com.devspace.rickandmorty

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@Composable
fun RickandMortyApp() {
    val navcontroller = rememberNavController()
    NavHost(navController = navcontroller, startDestination = "characterList") {
        composable("characterList") {
            CharacterListScreen(navController = navcontroller)
        }
        composable(
            route = "characterDetail/{characterId}",
            arguments = listOf(navArgument("characterId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val characterId = requireNotNull(backStackEntry.arguments?.getString("characterId"))
            CharacterDetailScreen(characterId = characterId)
        }
    }
}
