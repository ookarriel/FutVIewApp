package com.example.futview.data

import com.example.futview.data.repository.FirebaseAuthRepository
import com.example.futview.data.repository.FirestoreUserRepository
import com.example.futview.data.repository.MatchRepository
import com.example.futview.data.repository.StadiumRepository
import com.example.futview.data.repository.TeamRepository
import com.example.futview.data.repository.UserRepository
import com.example.futview.data.repository.AuthRepository
import com.example.futview.data.repository.EmptyMatchRepository
import com.example.futview.data.repository.EmptyStadiumRepository
import com.example.futview.data.repository.EmptyTeamRepository

/**
 * Inyección manual (sin Hilt): punto único donde se elige la implementación de cada repositorio.
 *
 * - `userRepository`: conectado a Firestore desde la Fase 2 (rol del usuario).
 * - Equipos, estadios y partidos: stubs vacíos hasta la Fase 3.
 */
object AppContainer {
    val userRepository: UserRepository = FirestoreUserRepository()
    val authRepository: AuthRepository = FirebaseAuthRepository(userRepository)
    val teamRepository: TeamRepository = EmptyTeamRepository()
    val stadiumRepository: StadiumRepository = EmptyStadiumRepository()
    val matchRepository: MatchRepository = EmptyMatchRepository()
}
