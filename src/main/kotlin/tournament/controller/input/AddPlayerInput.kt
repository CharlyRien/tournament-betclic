package tournament.controller.input

import javax.validation.constraints.NotEmpty

class AddPlayerInput(@NotEmpty val username: String)