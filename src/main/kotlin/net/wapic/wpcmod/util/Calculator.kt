package net.wapic.wpcmod.util

import net.wapic.wpcmod.WpcMod
import net.wapic.wpcmod.util.Utils.equalsOneOf

class Calculator(equation: String) {
	private val tokens =
		equation.replace(" ", "").split(Regex("(?<=[-+x*/()])|(?=[-+x*/()])")).filter { it.isNotEmpty() }
	private var pos = 0

	fun eval(): Int? {
		if (tokens.isEmpty()) return null
		try {
			return parseExpression()
		} catch (e: Exception) {
			WpcMod.LOGGER.error("Failed evaluate equation! $e")
			e.printStackTrace()
			return null
		}
	}

	private fun parseExpression(): Int {
		var result = parseTerm()
		while (pos < tokens.size && (tokens[pos] == "+" || tokens[pos] == "-")) {
			val op = tokens[pos++]
			val nextVal = parseTerm()
			result = if (op == "+") result + nextVal else result - nextVal
		}
		return result
	}

	private fun parseTerm(): Int {
		var result = parseFactor()
		while (pos < tokens.size && tokens[pos].equalsOneOf("x", "*", "/")) {
			val op = tokens[pos++]
			val nextVal = parseFactor()
			result = when (op) {
				"x", "*" -> result * nextVal
				else -> result / nextVal
			}
		}
		return result
	}

	private fun parseFactor(): Int {
		if (pos >= tokens.size) return 0
		val token = tokens[pos++]
		return if (token == "(") {
			val result = parseExpression()
			if (pos < tokens.size && tokens[pos] == ")") {
				pos++ // Skip ")"
			}
			result
		} else {
			token.toInt()
		}
	}
}
