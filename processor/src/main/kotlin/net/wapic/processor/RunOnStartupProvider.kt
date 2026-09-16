package net.wapic.processor

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class RunOnStartupProvider : SymbolProcessorProvider {

	override fun create(environment: SymbolProcessorEnvironment) = RunOnStartupProcessor(environment.codeGenerator)
}