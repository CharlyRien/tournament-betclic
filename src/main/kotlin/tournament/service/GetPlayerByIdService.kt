package tournament.service

import tournament.domain.Player
import java.util.*
import java.util.function.Function

interface GetPlayerByIdService : Function<UUID,Player>
