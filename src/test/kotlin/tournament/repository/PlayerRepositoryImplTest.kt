package tournament.repository

import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.WithAssertions
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import tournament.infrastructure.redis.RedisClient

@ExtendWith(MockKExtension::class)
internal class PlayerRepositoryImplTest : WithAssertions {

    private val redisClient: RedisClient = mockk()

    private val playerRepository: PlayerRepository = PlayerRepositoryImpl(redisClient)

    private var easyRandom: EasyRandom = EasyRandom()

    @BeforeEach
    fun setUp() {
        easyRandom = EasyRandom()
    }

    @Test
    @Disabled
    fun `when a player is added to the tournament his points should be 0`() {
        val username = easyRandom.nextObject(String::class.java)
        val playerAdded = playerRepository.insert(username)

        assertThat(playerAdded.points).isEqualTo(0)
    }

    @Test
    @Disabled
    fun `when a player is added to the tournament with already added players his ranking should be higher than players with negative points`() {
        val username1 = easyRandom.nextObject(String::class.java)
        val username2 = easyRandom.nextObject(String::class.java)

        val player1Added = playerRepository.insert(username1)
        playerRepository.applyPointsDelta(player1Added.id, 5)

        val player2Added = playerRepository.insert(username2)
        playerRepository.applyPointsDelta(player2Added.id, -5)

        val username3 = easyRandom.nextObject(String::class.java)
        val player3Added = playerRepository.insert(username3)
        assertThat(player3Added.ranking).isEqualTo(2)
    }
}