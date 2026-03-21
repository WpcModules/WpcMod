package net.wapic.wpcmod.util

import com.google.gson.JsonParser
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object APIUtils {

	fun fetch(uri: String): String? {
		val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()
		val request = HttpRequest.newBuilder().uri(URI.create(uri)).GET().build()

		try {
			val response = client.send(request, HttpResponse.BodyHandlers.ofString())
			if (response.statusCode() != 200) return null
			return response.body()
		} catch (e: Exception) {
			return null
		}
	}

	fun hasBonusPaulScore(): Boolean {
		val response = fetch("https://api.hypixel.net/resources/skyblock/election") ?: return false
		val jsonObject = JsonParser.parseString(response).toJsonObject() ?: return false
		if (jsonObject.getJsonPrimitive("success")?.asBoolean == true) {

			val mayor = jsonObject.getJsonObject("mayor") ?: return false
			val minister = mayor.getJsonObject("minister") ?: return false

			val name = mayor.getJsonPrimitive("name")?.asString
			val ministerName = minister.getJsonPrimitive("name")?.asString

			if (name == "Paul") {
				return mayor.getJsonArray("perks")?.any {
					it.toJsonObject()?.getJsonPrimitive("name")?.asString == "EZPZ"
				} ?: false
			} else if (ministerName == "Paul") {
				return minister.getAsJsonObject("perk")?.getJsonPrimitive("name")?.asString == "EZPZ"
			}
		}
		return false
	}
}