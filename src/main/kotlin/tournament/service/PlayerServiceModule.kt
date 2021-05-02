package tournament.service

import dagger.Module
import dagger.Provides
import tournament.repository.PlayerRepository
import javax.inject.Singleton

@Module
object PlayerServiceModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideAddPlayerToTournamentService(playerRepository: PlayerRepository): AddPlayerToTournamentService {
        return AddPlayerToTournamentServiceImpl(playerRepository)
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideDeleteAllPlayersToTournamentService(playerRepository: PlayerRepository): DeleteAllPlayersFromTournamentService {
        return DeleteAllPlayersFromTournamentServiceImpl(playerRepository)
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideGetAllPlayersToTournamentService(playerRepository: PlayerRepository): GetAllPlayersFromTournamentService {
        return GetAllPlayersFromTournamentServiceImpl(playerRepository)
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideUpdatePlayerPointsService(playerRepository: PlayerRepository): UpdatePlayerPointsService {
        return UpdatePlayerPointsServiceImpl(playerRepository)
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideGetPlayerByIdService(playerRepository: PlayerRepository): GetPlayerByIdService {
        return GetPlayerByIdServiceImpl(playerRepository)
    }
}