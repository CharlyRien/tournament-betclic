package tournament.domain

import java.util.*

data class Player(val id: UUID, val username: String, var points: Int, var ranking: Int)