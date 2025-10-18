package net.wapic.wpcmod.util

import com.google.gson.JsonParser
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

object APIUtils {
	fun fetch(uri: String): String? {
		HttpClients.createMinimal().use {
			try {
				val httpGet = HttpGet(uri)
				return EntityUtils.toString(it.execute(httpGet).entity)
			} catch (e: Exception) {
				return null
			}
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