package com.jolufeja.httpclient.error

import arrow.core.Either
import arrow.core.flatMap
import com.jolufeja.httpclient.HttpClientRequest
import com.jolufeja.httpclient.HttpClientResponse
import com.jolufeja.httpclient.awaitJsonBody
import com.jolufeja.httpclient.awaitJsonBodyOrNull


typealias Result<T> = Either<CommonErrors, T>

typealias ErrorConstructor = (Unit) -> CommonErrors

fun interface ErrorHandler<E> : (Throwable) -> E {
    override operator fun invoke(err: Throwable): E
}

interface MapDomain<T, E> : ErrorHandler<E> {
    suspend fun T.toDomain(): Either<E, T>
}


suspend fun HttpClientResponse.readErrorBody(ctor: ErrorConstructor): CommonErrors =
    awaitJsonBodyOrNull<ErrorBody>()?.let { (placeholder) ->
        ctor(placeholder)
    } ?: CommonErrors.RequestError(Throwable("Response body is empty - can't construct specific error instance."))


internal val HttpErrorHandler = ErrorHandler<CommonErrors>(CommonErrors::RequestError)

suspend fun HttpClientRequest.tryExecute(): Either<CommonErrors, HttpClientResponse> = with(CommonErrors.Companion) {
    Either
        .catch { awaitExecute() }
        .mapLeft(this)
        .flatMap { it.toDomain() }
}

suspend fun HttpClientRequest.Builder.tryExecute(): Either<CommonErrors, HttpClientResponse> =
    build().tryExecute()


inline fun <T> catchError(fn: () -> T): Either<CommonErrors, T> =
    Either.catch(fn).mapLeft(CommonErrors)

suspend inline fun <reified T : Any> Either<CommonErrors, HttpClientResponse>.awaitJsonBody() =
    flatMap { response -> catchError { response.awaitJsonBody<T>() } }


sealed interface CommonErrors {
    companion object : MapDomain<HttpClientResponse, CommonErrors> by TODO()
    data class RequestError(val cause: Throwable) : CommonErrors
}



