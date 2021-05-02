package tournament.controller

import dagger.Module
import dagger.Provides
import tournament.service.AddPlayerToTournamentService
import tournament.service.DeleteAllPlayersFromTournamentService
import tournament.service.GetAllPlayersFromTournamentService
import tournament.service.GetPlayerByIdService
import tournament.service.UpdatePlayerPointsService
import javax.inject.Singleton

@Module
object PlayerControllerModule {

    @Provides
    @Singleton
    @JvmStatic
    fun providePlayerController(
        addPlayerToTournamentService: AddPlayerToTournamentService,
        deleteAllPlayersFromTournamentService: DeleteAllPlayersFromTournamentService,
        getAllPlayersFromTournamentService: GetAllPlayersFromTournamentService,
        updatePlayerPointsService: UpdatePlayerPointsService,
        getPlayerByIdService: GetPlayerByIdService
    ): PlayerController {
        return PlayerController(
            addPlayerToTournamentService,
            deleteAllPlayersFromTournamentService,
            getAllPlayersFromTournamentService,
            updatePlayerPointsService,
            getPlayerByIdService
        )
    }
}