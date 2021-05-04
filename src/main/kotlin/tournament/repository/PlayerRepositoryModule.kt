package tournament.repository

import dagger.Module
import dagger.Provides
import tournament.infrastructure.redis.RedisClient
import javax.inject.Singleton

@Module
object PlayerRepositoryModule {

    @Provides
    @Singleton
    @JvmStatic
    fun providePlayerRepository(redisClient: RedisClient): PlayerRepository {
        return PlayerRepositoryImpl(redisClient)
    }
}