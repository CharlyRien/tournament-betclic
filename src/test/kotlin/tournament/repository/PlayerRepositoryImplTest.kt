package tournament.repository

import io.lettuce.core.KeyValue
import io.lettuce.core.Range
import io.lettuce.core.ScoredValue
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.WithAssertions
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import tournament.domain.Player
import tournament.infrastructure.redis.RedisClient
import java.util.*

@ExtendWith(MockKExtension::class)
internal class PlayerRepositoryImplTest : WithAssertions {
    private val expectedLeaderboardKey = "myLeaderboard"

    private val redisClient: RedisClient = mockk()
    private val redisConnection: StatefulRedisConnection<String, String> = mockk()
    private val redisCommands: RedisCommands<String, String> = mockk()

    private val playerRepository: PlayerRepository = PlayerRepositoryImpl(redisClient)

    private var easyRandom: EasyRandom = EasyRandom()

    @BeforeEach
    fun setUp() {
        easyRandom = EasyRandom()
        every { redisClient.connection } returns redisConnection
        every { redisConnection.sync() } returns redisCommands
    }

    @Test
    fun `when a player is added to the tournament his points should be 0`() {
        val username = easyRandom.nextObject(String::class.java)
        val rank: Long = 1

        val playerAdded = given_inserted_player(username, rank)

        then_player_should_have_zero_points(playerAdded, rank)
    }

    @Test
    fun `when a player is added to the tournament and his rank is not available then throw exception`() {
        val username = easyRandom.nextObject(String::class.java)
        every { redisCommands.set(any(), any(), any()) } returns "OK"
        every { redisCommands.zadd(expectedLeaderboardKey, any<ScoredValue<String>>()) } returns 1
        every { redisCommands.zrevrank(expectedLeaderboardKey, any()) } returns null

        assertThatThrownBy {
            playerRepository.insert(username)
        }.isInstanceOf(PlayerNotFoundException::class.java)
    }

    @Test
    fun `when apply delta to a player points then his points should increase or decrease`() {
        val username = easyRandom.nextObject(String::class.java)
        val playerId = UUID.randomUUID()
        val playerIdString = playerId.toString()

        every { redisCommands.zincrby(expectedLeaderboardKey, 5.0, playerIdString) } returns 5.0
        every { redisCommands.zrevrank(expectedLeaderboardKey, playerIdString) } returns 0
        every { redisCommands.get(playerIdString) } returns username

        val playerUpdated = playerRepository.applyPointsDelta(playerId, 5)

        assertThat(playerUpdated.points).isEqualTo(5)
        assertThat(playerUpdated.ranking).isEqualTo(1)
    }

    @Test
    fun `when apply delta to a player with no defined rank then should throw exception`() {
        val playerId = UUID.randomUUID()

        every { redisCommands.zincrby(expectedLeaderboardKey, any(), any()) } returns 5.0
        every { redisCommands.zrevrank(expectedLeaderboardKey, any()) } returns null

        assertThatThrownBy {
            playerRepository.applyPointsDelta(playerId, 5)
        }.isInstanceOf(PlayerNotFoundException::class.java)
    }

    @Test
    fun `when apply delta to a player with no username then should throw exception`() {
        val playerId = UUID.randomUUID()

        every { redisCommands.zincrby(expectedLeaderboardKey, any(), any()) } returns 5.0
        every { redisCommands.zrevrank(expectedLeaderboardKey, any()) } returns 1
        every { redisCommands.get(any()) } returns null

        assertThatThrownBy {
            playerRepository.applyPointsDelta(playerId, 5)
        }.isInstanceOf(PlayerNotFoundException::class.java)
    }

    @Test
    fun `when a player is getById it should be returned if it exists`() {
        val username = easyRandom.nextObject(String::class.java)
        val expectedPoints = 5
        val expectedRank: Long = 1
        val playerUpdated = when_player_get_by_id(UUID.randomUUID(), username, expectedPoints, expectedRank)

        assertThat(playerUpdated.points).isEqualTo(expectedPoints)
        assertThat(playerUpdated.ranking).isEqualTo(expectedRank)
    }

    @Test
    fun `when a player is getById with exception thrown when retrieving rank then it should throw exception`() {
        every { redisCommands.zscore(expectedLeaderboardKey, any()) } returns 5.0
        every { redisCommands.zrevrank(expectedLeaderboardKey, any()) } returns null

        assertThatThrownBy {
            playerRepository.getById(UUID.randomUUID())
        }.isInstanceOf(PlayerNotFoundException::class.java)
    }

    @Test
    fun `when a player is getById with exception thrown when retrieving score then it should throw exception`() {
        every { redisCommands.zscore(expectedLeaderboardKey, any()) } returns null

        assertThatThrownBy {
            playerRepository.getById(UUID.randomUUID())
        }.isInstanceOf(PlayerNotFoundException::class.java)
    }

    @Test
    fun `when a player is getById with exception thrown when retrieving username then it should throw exception`() {
        every { redisCommands.zscore(expectedLeaderboardKey, any()) } returns 5.0
        every { redisCommands.zrevrank(expectedLeaderboardKey, any()) } returns 0
        every { redisCommands.get(any()) } returns null

        assertThatThrownBy {
            playerRepository.getById(UUID.randomUUID())
        }.isInstanceOf(PlayerNotFoundException::class.java)
    }

    @Test
    fun `when delete all players with no exception thrown then no players should exists`() {
        every { redisCommands.flushall() } returns "OK"

        playerRepository.deleteAll()

        verify(exactly = 1) { redisCommands.flushall() }
    }

    @Test
    fun `when get all players then return them sorted by score`() {
        val player1 = UUID.randomUUID()
        val player2 = UUID.randomUUID()
        val usernamePlayer1 = easyRandom.nextObject(String::class.java)
        val usernamePlayer2 = easyRandom.nextObject(String::class.java)
        val expectedScoredValues = listOf(
            ScoredValue.just(10.0, player1.toString()),
            ScoredValue.just(5.0, player2.toString())
        )

        every {
            redisCommands.zrevrangebyscoreWithScores(
                expectedLeaderboardKey,
                Range.unbounded()
            )
        } returns expectedScoredValues

        val expectedPlayerMultipleKeyValue = listOf(
            KeyValue.just(player1.toString(), usernamePlayer1),
            KeyValue.just(player2.toString(), usernamePlayer2)
        )

        every {
            redisCommands.mget(player1.toString(), player2.toString())
        } returns expectedPlayerMultipleKeyValue

        val allPlayersSortedByRank = playerRepository.getAllPlayersSortedByRank()

        assertThat(allPlayersSortedByRank)
            .isSortedAccordingTo { o1, o2 -> o2.points.compareTo(o1.points) }
    }

    @Test
    fun `when get all players with no players into the leaderboard then should return an empty list`() {
        every {
            redisCommands.zrevrangebyscoreWithScores(
                expectedLeaderboardKey,
                Range.unbounded()
            )
        } returns listOf()

        assertThat(playerRepository.getAllPlayersSortedByRank()).isEmpty()
    }

    private fun given_inserted_player(username: String, rank: Long): Player {
        every { redisCommands.set(any(), username, any()) } returns "OK"
        every { redisCommands.zadd(expectedLeaderboardKey, any<ScoredValue<String>>()) } returns 1
        every { redisCommands.zrevrank(expectedLeaderboardKey, any()) } returns rank - 1

        return playerRepository.insert(username)
    }

    private fun then_player_should_have_zero_points(playerAdded: Player, expectedRank: Long) {
        assertThat(playerAdded.points).isEqualTo(0)
        assertThat(playerAdded.ranking).isEqualTo(expectedRank)
    }

    private fun when_player_get_by_id(
        playerId: UUID,
        username: String,
        points: Int,
        rank: Long
    ): Player {
        val playerIdString = playerId.toString()
        every { redisCommands.zscore(expectedLeaderboardKey, playerIdString) } returns points.toDouble()
        every { redisCommands.zrevrank(expectedLeaderboardKey, playerIdString) } returns rank - 1
        every { redisCommands.get(playerIdString) } returns username

        return playerRepository.getById(playerId)
    }
}