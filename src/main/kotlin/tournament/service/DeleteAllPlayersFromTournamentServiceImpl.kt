package tournament.service

import tournament.repository.PlayerRepository
import javax.inject.Inject

class DeleteAllPlayersFromTournamentServiceImpl @Inject constructor(private val playerRepository: PlayerRepository) : DeleteAllPlayersFromTournamentService {
    override fun execute() {
        playerRepository.deleteAll()
    }
}