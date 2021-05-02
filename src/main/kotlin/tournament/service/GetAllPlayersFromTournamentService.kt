package tournament.service

import tournament.domain.Player
import java.util.function.Supplier

interface GetAllPlayersFromTournamentService : Supplier<List<Player>>
