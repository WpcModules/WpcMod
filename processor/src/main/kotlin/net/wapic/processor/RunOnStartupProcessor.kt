package net.wapic.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class RunOnStartupProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {

	override fun process(resolver: Resolver): List<KSAnnotated> {
		val functions = resolver.getSymbolsWithAnnotation("net.wapic.wpcmod.util.RunOnStartup").filterIsInstance<KSFunctionDeclaration>().toList()
		if (functions.isEmpty()) return emptyList()

		codeGenerator.createNewFile(Dependencies(false), "net.wapic.wpcmod.generated", "StartupFunctions").writer().use { out ->
			out.write("package net.wapic.wpcmod.generated\n\n")

			out.write("fun runStartupFunctions() {\n")
			for (func in functions) {
				val funcName = func.qualifiedName!!.asString()
				out.write("		$funcName()\n")
			}
			out.write("}\n")
		}

		return emptyList()
	}
}
