package tournament.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.WithAssertions
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tournament.domain.Player
import tournament.repository.PlayerRepository

internal class GetAllPlayersFromTournamentServiceImplTest: WithAssertions {
    private val playerRepository: PlayerRepository = mockk()
    private var easyRandom: EasyRandom = EasyRandom()

    private val getAllPlayersFromTournamentService : GetAllPlayersFromTournamentService = GetAllPlayersFromTournamentServiceImpl(playerRepository)

    @BeforeEach
    fun setUp() {
        easyRandom = EasyRandom()
    }

    @Test
    fun `when service is called then verify repository returned object is the same than the object returned by the service`() {
        val player = easyRandom.nextObject(Player::class.java)
        val expectedPlayers = listOf(player)

        every { playerRepository.getAllPlayersSortedByRank() } returns expectedPlayers

        assertThat(getAllPlayersFromTournamentService.get()).isEqualTo(expectedPlayers)

        verify(exactly = 1) { playerRepository.getAllPlayersSortedByRank() }
    }
}