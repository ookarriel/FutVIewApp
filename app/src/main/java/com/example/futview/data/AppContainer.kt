package com.example.futview.data

import com.example.futview.data.repository.FirebaseAuthRepository
import com.example.futview.data.repository.FirestoreFavoritesRepository
import com.example.futview.data.repository.FirestoreMatchRepository
import com.example.futview.data.repository.FirestoreStadiumRepository
import com.example.futview.data.repository.FirestoreTeamRepository
import com.example.futview.data.repository.FirestoreUserRepository
import com.example.futview.data.repository.AuthRepository
import com.example.futview.data.repository.FavoritesRepository
import com.example.futview.data.repository.MatchRepository
import com.example.futview.data.repository.StadiumRepository
import com.example.futview.data.repository.TeamRepository
import com.example.futview.data.repository.UserRepository

/**
 * Inyección manual (sin Hilt): punto único donde se elige la implementación de cada repositorio.
 * Todos los repositorios leen y escriben en Firestore.
 */
object AppContainer {
    val userRepository: UserRepository = FirestoreUserRepository()
    val authRepository: AuthRepository = FirebaseAuthRepository(userRepository)
    val teamRepository: TeamRepository = FirestoreTeamRepository()
    val stadiumRepository: StadiumRepository = FirestoreStadiumRepository()
    val matchRepository: MatchRepository = FirestoreMatchRepository()
    val favoritesRepository: FavoritesRepository = FirestoreFavoritesRepository()
}
