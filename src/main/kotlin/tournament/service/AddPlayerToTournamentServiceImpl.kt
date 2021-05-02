package tournament.service

import tournament.domain.Player
import tournament.repository.PlayerRepository
import javax.inject.Inject

class AddPlayerToTournamentServiceImpl @Inject constructor(private val playerRepository: PlayerRepository) : AddPlayerToTournamentService {
    override fun apply(username: String): Player {
        return playerRepository.insert(username)
    }
}
