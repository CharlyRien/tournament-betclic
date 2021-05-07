package tournament

import TournamentApp
import TournamentConfiguration
import io.dropwizard.configuration.FileConfigurationSourceProvider
import io.dropwizard.testing.ConfigOverride
import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.assertj.core.api.WithAssertions
import org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400
import org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404
import org.eclipse.jetty.http.HttpStatus.NO_CONTENT_204
import org.eclipse.jetty.http.HttpStatus.OK_200
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import tournament.domain.Player
import tournament.infrastructure.redis.RedisClient
import tournament.repository.PlayerRepository
import tournament.repository.PlayerRepositoryImpl
import java.util.*
import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType


@ExtendWith(DropwizardExtensionsSupport::class)
@Testcontainers
class TournamentAppIntegrationTests : WithAssertions {

    private val redisClient = RedisClient(
       io.lettuce.core.RedisClient
           .create("redis://${redis.host}:${redis.firstMappedPort}")
           .connect()
    )

    private val dropwizardAppExtension: DropwizardAppExtension<TournamentConfiguration> = DropwizardAppExtension(
        TournamentApp::class.java,
        ResourceHelpers.resourceFilePath("config-test.yaml"),
        FileConfigurationSourceProvider(),
        ConfigOverride.config("redis.node.node", "${redis.host}:${redis.firstMappedPort}")
    )

    private val playerRepository: PlayerRepository = PlayerRepositoryImpl(redisClient)

    @BeforeEach
    fun clear() {
        playerRepository.deleteAll()
    }

    @Test
    fun `when POST for adding a new player without input response should return 400 BAD REQUEST`() {
        val client = dropwizardAppExtension.client()
        val response = client.target(String.format("http://localhost:%d/api/players", dropwizardAppExtension.localPort))
            .request()
            .post(Entity.json("{}"))
        assertThat(response.status).isEqualTo(BAD_REQUEST_400)
    }

    @Test
    fun `when POST for adding a new player then response should return 200 OK with the player inserted`() {
        val client = dropwizardAppExtension.client()

        val expectedUsername="myUsername"
        val jsonPlayerInput = """{
            | "username": "$expectedUsername"
            |}""".trimMargin()

        val response = client.target(String.format("http://localhost:%d/api/players", dropwizardAppExtension.localPort))
            .request()
            .post(Entity.json(jsonPlayerInput))

        assertThat(response.status).isEqualTo(OK_200)
        assertThat(response.hasEntity()).isTrue

        val (id, actualUsername, points, ranking) = response.readEntity(Player::class.java)
        assertThat(actualUsername).isEqualTo(expectedUsername)
        assertThat(id).isNotNull
        assertThat(points).isEqualTo(0)
        assertThat(ranking).isEqualTo(1)
    }

    @Test
    fun `when PATCH to modify points to a player that not exists then response should return 404`() {
        val client = dropwizardAppExtension.client()

        val jsonPlayerInput = """{
            | "delta": -5
            |}""".trimMargin()

        val randomUUID = UUID.randomUUID().toString()
        val response = client.target(String.format("http://localhost:%d/api/players/%s/points", dropwizardAppExtension.localPort, randomUUID))
            .request()
            .method("PATCH", Entity.json(jsonPlayerInput))

        assertThat(response.status).isEqualTo(NOT_FOUND_404)
    }

    @Test
    fun `when PATCH to modify points to a player with no json body then response should return 400`() {
        val client = dropwizardAppExtension.client()

        val randomUUID = UUID.randomUUID().toString()
        val response = client.target(String.format("http://localhost:%d/api/players/%s/points", dropwizardAppExtension.localPort, randomUUID))
            .request()
            .method("PATCH", Entity.json("{}"))

        assertThat(response.status).isEqualTo(BAD_REQUEST_400)
    }

    @Test
    fun `when PATCH to modify points to a player with json body with points containing null then response should return 400`() {
        val client = dropwizardAppExtension.client()

        val randomUUID = UUID.randomUUID().toString()
        val inputWithNullDelta = """{
                | "delta": null
                |}""".trimMargin()
        val response = client.target(String.format("http://localhost:%d/api/players/%s/points", dropwizardAppExtension.localPort, randomUUID))
            .request()
            .method("PATCH", Entity.json(inputWithNullDelta))

        assertThat(response.status).isEqualTo(BAD_REQUEST_400)
    }

    @Test
    fun `when PATCH to modify points to a player with valid body then response should return 200 with the player with points modification applied`() {
        val client = dropwizardAppExtension.client()

        val username = "myUsername"
        val player = playerRepository.insert(username)
        val delta = -5
        val jsonPlayerInput = """{
            | "delta": $delta
            |}""".trimMargin()

        val response = client.target(String.format("http://localhost:%d/api/players/%s/points", dropwizardAppExtension.localPort, player.id))
            .request()
            .build("PATCH", Entity.json(jsonPlayerInput))
            .invoke()

        assertThat(response.status).isEqualTo(OK_200)
        assertThat(response.hasEntity()).isTrue

        val (id, actualUsername, points, ranking) = response.readEntity(Player::class.java)
        assertThat(actualUsername).isEqualTo(username)
        assertThat(id).isEqualTo(player.id)
        assertThat(points).isEqualTo(delta)
        assertThat(ranking).isEqualTo(1)
    }

    @Test
    fun `when DELETE to delete all players with no players existing then response should return 200`() {
        val client = dropwizardAppExtension.client()

        val response = client.target(String.format("http://localhost:%d/api/players", dropwizardAppExtension.localPort))
            .request()
            .delete()

        assertThat(response.status).isEqualTo(NO_CONTENT_204)
    }

    @Test
    fun `when DELETE to delete all players with players existing then response should return 200`() {
        val client = dropwizardAppExtension.client()

        playerRepository.insert("username1")
        playerRepository.insert("username2")
        playerRepository.insert("username3")

        assertThat(playerRepository.getAllPlayersSortedByRank()).hasSize(3)

        val response = client.target(String.format("http://localhost:%d/api/players", dropwizardAppExtension.localPort))
            .request()
            .delete()

        assertThat(response.status).isEqualTo(NO_CONTENT_204)
        assertThat(playerRepository.getAllPlayersSortedByRank()).hasSize(0)
    }

    @Test
    fun `when GET to retrieve all players with no players existing then response should return 200 with empty array`() {
        val client = dropwizardAppExtension.client()

        val playerListType = object : GenericType<List<Player>>() {}
        val response = client.target(String.format("http://localhost:%d/api/players", dropwizardAppExtension.localPort))
            .request()
            .get()

        assertThat(response.status).isEqualTo(OK_200)

        val playerList = response.readEntity(playerListType)
        assertThat(playerList).hasSize(0)
    }

    @Test
    fun `when GET to retrieve all players with players existing then response should return 200 with array sorted by points (higher to lower)`() {
        val client = dropwizardAppExtension.client()

        val insertedPlayers = insertPlayers(
            Pair("username1", 50),
            Pair("username2", -30),
            Pair("username5", 5600),
            Pair("username3", -20),
            Pair("username4", 180)
        )

        val playerListType = object : GenericType<List<Player>>() {}
        val response = client.target(String.format("http://localhost:%d/api/players", dropwizardAppExtension.localPort))
            .request()
            .get()

        assertThat(response.status).isEqualTo(OK_200)

        val playerList = response.readEntity(playerListType)
        assertThat(playerList).hasSize(5)
        assertThat(playerList.map { it.username }).containsAll(insertedPlayers.map { it.username })
        assertThat(playerList).isSortedAccordingTo { player1, player2 -> player2.points.compareTo(player1.points) }
    }

    @Test
    fun `when GET to retrieve a player by id not existing then response should return 200 and return the player`() {
        val client = dropwizardAppExtension.client()

        val expectedPlayer = playerRepository.insert("myUsername")

        val response = client.target(String.format("http://localhost:%d/api/players/%s", dropwizardAppExtension.localPort, expectedPlayer.id))
            .request()
            .get()

        assertThat(response.status).isEqualTo(OK_200)
        val actualPlayer = response.readEntity(Player::class.java)
        assertThat(actualPlayer).isEqualTo(expectedPlayer)
    }

    @Test
    fun `when GET to retrieve a player by id existing then response should return 404`() {
        val client = dropwizardAppExtension.client()

        val response = client.target(String.format("http://localhost:%d/api/players/%s", dropwizardAppExtension.localPort, UUID.randomUUID().toString()))
            .request()
            .get()

        assertThat(response.status).isEqualTo(NOT_FOUND_404)
    }

    private fun insertPlayers(vararg usernameToPoints:Pair<String, Int>) : List<Player> {
        return usernameToPoints.map {
            val (id, _, _, _) = playerRepository.insert(it.first)
            playerRepository.applyPointsDelta(id, it.second)
        }
    }

    companion object {
        @Container
        @JvmStatic
        private val redis = GenericContainer<GenericContainer<*>>(DockerImageName.parse("redis:6.2.3-alpine"))
                .withExposedPorts(6379)
    }
}