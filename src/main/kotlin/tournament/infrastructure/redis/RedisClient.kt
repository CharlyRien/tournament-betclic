package tournament.infrastructure.redis

import io.lettuce.core.api.StatefulRedisConnection
import javax.inject.Inject

class RedisClient @Inject constructor(val connection: StatefulRedisConnection<String, String>)