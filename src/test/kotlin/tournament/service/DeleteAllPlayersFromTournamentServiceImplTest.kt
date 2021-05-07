package tournament.service

import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.Test
import tournament.repository.PlayerRepository

internal class DeleteAllPlayersFromTournamentServiceImplTest: WithAssertions {
    private val playerRepository: PlayerRepository = mockk()

    private val deleteAllPlayersFromTournamentService : DeleteAllPlayersFromTournamentService = DeleteAllPlayersFromTournamentServiceImpl(playerRepository)

    @Test
    fun `when service is called then verify repository is called`() {
        justRun { playerRepository.deleteAll() }

        deleteAllPlayersFromTournamentService.execute()

        verify(exactly = 1) { playerRepository.deleteAll() }
    }
}