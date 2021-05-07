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

internal class GetPlayerByIdServiceImplTest : WithAssertions {
    private val playerRepository: PlayerRepository = mockk()
    private var easyRandom: EasyRandom = EasyRandom()

    private val getPlayerByIdService: GetPlayerByIdService =
        GetPlayerByIdServiceImpl(playerRepository)

    @BeforeEach
    fun setUp() {
        easyRandom = EasyRandom()
    }

    @Test
    fun `when service is called then verify repository returned object is the same than the object returned by the service`() {
        val player = easyRandom.nextObject(Player::class.java)
        val playerId = player.id

        every { playerRepository.getById(playerId) } returns player

        assertThat(getPlayerByIdService.apply(playerId)).isEqualTo(player)

        verify(exactly = 1) { playerRepository.getById(playerId) }
    }
}