package tournament.service

import tournament.domain.Player
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function

interface UpdatePlayerPointsService : BiFunction<UUID, Int, Player>
