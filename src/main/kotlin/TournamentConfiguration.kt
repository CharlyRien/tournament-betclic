import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.redis.RedisClientFactory
import javax.validation.Valid
import javax.validation.constraints.NotNull

class TournamentConfiguration(
    @field:JsonProperty("redis")
    @NotNull
    @Valid
    val redisClientFactory: RedisClientFactory<String, String>?
) : Configuration()