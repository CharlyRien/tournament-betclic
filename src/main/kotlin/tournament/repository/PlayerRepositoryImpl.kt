package tournament.repository

import io.lettuce.core.Range
import io.lettuce.core.ScoredValue
import io.lettuce.core.SetArgs
import tournament.domain.Player
import tournament.infrastructure.redis.RedisClient
import java.util.*
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val redisClient: RedisClient
) : PlayerRepository {

    override fun insert(username: String): Player {
        val playerId = UUID.randomUUID()
        val playerIdString = playerId.toString()

        return username
            .let {
                getRedisCommands()
                    .set(playerIdString, username, SetArgs().nx())
                    .apply { if (this != "OK") throw IllegalStateException("Cannot add player to tournament") }

                getRedisCommands()
                    .zadd(redisLeaderboardKey, ScoredValue.just(0.0, playerIdString))

                val rank = getRedisCommands()
                    .zrank(redisLeaderboardKey, playerIdString)?.plus(1)
                    ?: throw IllegalStateException("Player rank not available")

                Player(playerId, username, 0, rank.toInt())
            }
    }

    override fun applyPointsDelta(playerId: UUID, delta: Int): Player {
        val playerIdString = playerId.toString()
        val playerScore = getRedisCommands()
            .zincrby(redisLeaderboardKey, delta.toDouble(), playerIdString)

        val playerRank = getRedisCommands()
            .zrevrank(redisLeaderboardKey, playerIdString)?.plus(1)
            ?: throw PlayerNotFoundException()

        return getRedisCommands().get(playerIdString)
            ?.let { Player(playerId, it, playerScore.toInt(), playerRank.toInt()) }
            ?: throw PlayerNotFoundException()
    }

    override fun getById(playerId: UUID): Player {
        val playerIdString = playerId.toString()
        val playerScore = getRedisCommands()
            .zscore(redisLeaderboardKey, playerIdString)
            ?: throw PlayerNotFoundException()

        val playerRank = getRedisCommands()
            .zrevrank(redisLeaderboardKey, playerIdString)?.plus(1)
            ?: throw PlayerNotFoundException()

        val username = getRedisCommands().get(playerId.toString())
        return username?.let {
            Player(playerId, username, playerScore.toInt(), playerRank.toInt())
        } ?: throw PlayerNotFoundException()
    }

    override fun getAllPlayersSortedByRank(): List<Player> {
        val leaderBoardWithScores = getRedisCommands()
            .zrevrangebyscoreWithScores(redisLeaderboardKey, Range.unbounded()) ?: throw IllegalStateException("No Leaderboard found !")

        val allPlayersUsername = getRedisCommands()
            .mget(*leaderBoardWithScores.map { it.value }.toTypedArray())

        return leaderBoardWithScores
            .mapIndexed { index, scoredValue ->
                Player(
                    UUID.fromString(allPlayersUsername[index].key),
                    allPlayersUsername[index].value,
                    scoredValue.score.toInt(),
                    index + 1
                )
            }
    }

    override fun deleteAll() {
        getRedisCommands()
            .flushall()
    }

    private fun getRedisCommands() = redisClient.connection.sync()

}

private const val redisLeaderboardKey: String = "testLeaderboard"

class PlayerNotFoundException : java.lang.IllegalArgumentException("Player not found")