package tournament.repository

import tournament.domain.Player
import java.util.*

interface PlayerRepository {
    fun insert(username: String) : Player
    fun applyPointsDelta(playerId: UUID, delta: Int) : Player
    fun getById(playerId: UUID) : Player
    fun getAllPlayersSortedByRank() : List<Player>
    fun deleteAll()
}
