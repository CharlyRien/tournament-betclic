package tournament.controller

import javax.validation.constraints.NotEmpty

class UpdatePlayerPointsInput(@NotEmpty val delta: Int)