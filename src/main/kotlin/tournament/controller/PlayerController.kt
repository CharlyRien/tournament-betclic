package tournament.controller

import tournament.controller.input.AddPlayerInput
import tournament.controller.input.UpdatePlayerPointsInput
import tournament.controller.output.PlayerOutput
import tournament.domain.Player
import tournament.service.AddPlayerToTournamentService
import tournament.service.DeleteAllPlayersFromTournamentService
import tournament.service.GetAllPlayersFromTournamentService
import tournament.service.GetPlayerByIdService
import tournament.service.UpdatePlayerPointsService
import java.util.*
import javax.inject.Inject
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.PATCH
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/")
class PlayerController @Inject constructor(private val addPlayerToTournamentService: AddPlayerToTournamentService,
                                           private val deleteAllPlayersFromTournamentService: DeleteAllPlayersFromTournamentService,
                                           private val getAllPlayersFromTournamentService: GetAllPlayersFromTournamentService,
                                           private val updatePlayerPointsService: UpdatePlayerPointsService,
                                           private val getPlayerByIdService: GetPlayerByIdService

) {

    @Path("/players")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun addPlayerToTournament(@NotNull @Valid addPlayerInput: AddPlayerInput): PlayerOutput {
        return addPlayerToTournamentService
            .apply(addPlayerInput.username)
            .toPlayerOutput()
    }

    @Path("/players")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllPlayers(): List<PlayerOutput> {
        return getAllPlayersFromTournamentService
            .get()
            .toPlayerOutputList()
    }

    @Path("/players/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getPlayerById(@PathParam("id") playerId: UUID): PlayerOutput {
        return getPlayerByIdService
            .apply(playerId)
            .toPlayerOutput()
    }

    @Path("/players/{id}/points")
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    fun updatePlayerPoints(@PathParam("id") playerId: UUID,
                           @NotNull playerPointsUpdateInput: UpdatePlayerPointsInput
    ): PlayerOutput {
        return updatePlayerPointsService
            .apply(playerId, playerPointsUpdateInput.delta)
            .toPlayerOutput()
    }

    @Path("/players")
    @DELETE
    fun deleteAll() {
        deleteAllPlayersFromTournamentService.execute()
    }
}

private fun Player.toPlayerOutput() = PlayerOutput(id, username, ranking, points)
private fun List<Player>.toPlayerOutputList() = this.map { PlayerOutput(it.id, it.username, it.ranking, it.points) }
