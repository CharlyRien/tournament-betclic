package tournament.controller.output

import java.util.*

data class PlayerOutput(val id: UUID, val username: String, val ranking: Int, val points: Int)
