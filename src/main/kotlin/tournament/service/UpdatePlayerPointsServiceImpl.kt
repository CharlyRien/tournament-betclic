package tournament.service

import tournament.domain.Player
import tournament.repository.PlayerRepository
import java.util.*
import javax.inject.Inject

class UpdatePlayerPointsServiceImpl @Inject constructor(private val playerRepository: PlayerRepository) : UpdatePlayerPointsService {
    override fun apply(playerId: UUID, delta: Int): Player {
        return playerRepository.applyPointsDelta(playerId, delta)
    }
}