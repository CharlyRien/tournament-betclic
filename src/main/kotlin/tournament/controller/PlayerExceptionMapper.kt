package tournament.controller

import org.eclipse.jetty.http.HttpStatus
import tournament.repository.PlayerException
import tournament.repository.PlayerNotFoundException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class PlayerExceptionMapper : ExceptionMapper<PlayerException> {
    override fun toResponse(exception: PlayerException): Response {
        val httpStatus = when(exception) {
            is PlayerNotFoundException -> HttpStatus.NOT_FOUND_404
            else -> HttpStatus.INTERNAL_SERVER_ERROR_500
        }

        return Response
            .status(httpStatus)
            .entity(ErrorResponse(exception.message))
            .build()
    }
}

data class ErrorResponse(val message: String?)