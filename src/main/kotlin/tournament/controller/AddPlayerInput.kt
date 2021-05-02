package tournament.controller

import javax.validation.constraints.NotEmpty

class AddPlayerInput(@NotEmpty val username: String)