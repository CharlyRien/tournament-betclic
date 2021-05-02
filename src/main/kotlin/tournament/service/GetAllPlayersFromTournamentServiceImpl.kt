package tournament.service

import tournament.domain.Player
import tournament.repository.PlayerRepository
import javax.inject.Inject

class GetAllPlayersFromTournamentServiceImpl @Inject constructor(private val playerRepository: PlayerRepository) : GetAllPlayersFromTournamentService {
    override fun get(): List<Player> {
        return playerRepository.getAllPlayersSortedByRank()
    }
}