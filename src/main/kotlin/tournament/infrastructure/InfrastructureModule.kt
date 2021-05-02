package tournament.infrastructure

import dagger.Module
import dagger.Provides
import io.dropwizard.redis.RedisClientBundle
import io.lettuce.core.api.StatefulRedisConnection
import tournament.infrastructure.redis.RedisClient
import tournament.repository.PlayerRepository
import javax.inject.Singleton

@Module
object InfrastructureModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideRedisClient(redisConnection: StatefulRedisConnection<String, String>): RedisClient {
        return RedisClient(redisConnection)
    }
}