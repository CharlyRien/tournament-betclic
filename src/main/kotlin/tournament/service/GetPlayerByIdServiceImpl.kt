package tournament.service

import tournament.domain.Player
import tournament.repository.PlayerRepository
import java.util.*
import javax.inject.Inject

class GetPlayerByIdServiceImpl @Inject constructor(private val playerRepository: PlayerRepository) : GetPlayerByIdService {
    override fun apply(playerId: UUID): Player {
        return playerRepository.getById(playerId)
    }
}
