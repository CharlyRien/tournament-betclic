import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dagger.BindsInstance
import dagger.Component
import io.dropwizard.Application
import io.dropwizard.assets.AssetsBundle
import io.dropwizard.configuration.ResourceConfigurationSourceProvider
import io.dropwizard.redis.RedisClientBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.lettuce.core.api.StatefulRedisConnection
import org.eclipse.jetty.servlets.CrossOriginFilter
import tournament.controller.PlayerController
import tournament.controller.PlayerControllerModule
import tournament.controller.PlayerExceptionMapper
import tournament.repository.PlayerRepositoryModule
import tournament.service.PlayerServiceModule
import java.util.*
import javax.inject.Singleton
import javax.servlet.DispatcherType


fun main(args: Array<String>) {
    TournamentApp().run(*args)
}

class TournamentApp : Application<TournamentConfiguration>() {
    override fun run(configuration: TournamentConfiguration, environment: Environment) {
        val tournamentAppComponent = DaggerTournamentAppComponent
            .builder()
            .redisConnection(redisBundle.connection)
            .build()
        environment.jersey().register(PlayerExceptionMapper())
        environment.jersey().register(tournamentAppComponent.playerController())
        environment.enableCORSFilter()
    }

    private val redisBundle = object : RedisClientBundle<String, String, TournamentConfiguration>() {
        override fun getRedisClientFactory(configuration: TournamentConfiguration) =
            configuration.redisClientFactory
    }

    override fun initialize(bootstrap: Bootstrap<TournamentConfiguration>) {
        val objectMapper = bootstrap.objectMapper
        objectMapper.registerKotlinModule()
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
        bootstrap.configurationSourceProvider = ResourceConfigurationSourceProvider()
        bootstrap.addBundle(redisBundle)
        bootstrap.addBundle(AssetsBundle("/web/leaderboard/", "/"))
    }
}

private fun Environment.enableCORSFilter() {
    val filter = this.servlets().addFilter("CORS", CrossOriginFilter::class.java)
    filter.setInitParameter("allowedOrigins", "*")
    filter.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin")
    filter.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD")
    filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType::class.java), true, "/*")
}

@Singleton
@Component(modules = [PlayerControllerModule::class, PlayerServiceModule::class, PlayerRepositoryModule::class])
interface TournamentAppComponent {
    fun playerController(): PlayerController

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun redisConnection(redisConnection: StatefulRedisConnection<String, String>): Builder
        fun build(): TournamentAppComponent
    }
}