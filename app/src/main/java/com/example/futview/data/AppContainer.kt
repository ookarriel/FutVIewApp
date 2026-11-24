package com.example.futview.data

import com.example.futview.data.repository.EmptyMatchRepository
import com.example.futview.data.repository.EmptyStadiumRepository
import com.example.futview.data.repository.EmptyTeamRepository
import com.example.futview.data.repository.EmptyUserRepository
import com.example.futview.data.repository.MatchRepository
import com.example.futview.data.repository.StadiumRepository
import com.example.futview.data.repository.TeamRepository
import com.example.futview.data.repository.UserRepository

/**
 * Inyección manual (sin Hilt): punto único donde se elige la implementación de cada repositorio.
 *
 * Fase 3: cambiar las implementaciones vacías por las de Firestore, por ejemplo:
 * `val teamRepository: TeamRepository = FirestoreTeamRepository()`
 */
object AppContainer {
    val teamRepository: TeamRepository = EmptyTeamRepository()
    val stadiumRepository: StadiumRepository = EmptyStadiumRepository()
    val matchRepository: MatchRepository = EmptyMatchRepository()
    val userRepository: UserRepository = EmptyUserRepository()
}
