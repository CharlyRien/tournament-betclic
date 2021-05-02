package tournament.controller

import org.eclipse.jetty.http.HttpStatus
import tournament.repository.PlayerNotFoundException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class PlayerNotFoundExceptionMapper : ExceptionMapper<PlayerNotFoundException> {
    override fun toResponse(exception: PlayerNotFoundException): Response {
        return Response
            .status(HttpStatus.NOT_FOUND_404)
            .entity(ErrorResponse(exception.message))
            .build()
    }
}

data class ErrorResponse(val message: String?)