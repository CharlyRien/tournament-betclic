package tournament.service

import tournament.domain.Player
import java.util.function.Function

interface AddPlayerToTournamentService : Function<String, Player>
