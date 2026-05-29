package ru.mishlachok.LMessageClient.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import ru.mishlachok.LMessageClient.data.datastore.TokenStorage
import ru.mishlachok.LMessageClient.data.repository.PartData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiService @Inject constructor(
	val tokenStorage: TokenStorage
) {
	companion object {
		const val BASE_URL = "http://10.0.2.2:8080"
	}

	private val json = Json {
		ignoreUnknownKeys = true
		isLenient = true
		prettyPrint = false
	}

	val client = HttpClient {
		install(ContentNegotiation) {
			json(json)
		}
		install(Logging) {
			level = LogLevel.BODY
		}
		defaultRequest {
			url(BASE_URL)
			contentType(ContentType.Application.Json)
		}
	}

	@PublishedApi
	internal suspend fun bearerAuth(): String? {
		val token = tokenStorage.getToken()
		return if (token != null) "Bearer $token" else null
	}

	suspend inline fun <reified T> get(
		path: String,
		params: Map<String, String?> = emptyMap()
	): T {
		val auth = bearerAuth()
		return client.get {
			url(path)
			if (auth != null) header(HttpHeaders.Authorization, auth)
			params.forEach { (k, v) -> v?.let { parameter(k, it) } }
		}.body()
	}

	suspend inline fun <reified T, reified R> post(path: String, body: R): T {
		val auth = bearerAuth()
		return client.post {
			url(path)
			if (auth != null) header(HttpHeaders.Authorization, auth)
			setBody(body)
		}.body()
	}

	suspend inline fun <reified T> postMultipart(path: String, parts: List<PartData>): T {
		val auth = bearerAuth()
		return client.post {
			url(path)
			if (auth != null) header(HttpHeaders.Authorization, auth)
			setBody(MultiPartFormDataContent(formData {
				parts.forEach { part ->
					append(part.key, part.value, part.headers)
				}
			}))
		}.body()
	}

	suspend fun put(path: String, params: Map<String, String?> = emptyMap()): HttpResponse {
		val auth = bearerAuth()
		return client.put {
			url(path)
			if (auth != null) header(HttpHeaders.Authorization, auth)
			params.forEach { (k, v) -> v?.let { parameter(k, it) } }
		}
	}
	suspend inline fun <reified T> delete(path: String): T {
		val auth = bearerAuth()
		return client.delete {
			url(path)
			if (auth != null) header(HttpHeaders.Authorization, auth)
		}.body()
	}
}