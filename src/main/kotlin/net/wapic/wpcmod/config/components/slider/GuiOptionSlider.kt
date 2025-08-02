package net.wapic.wpcmod.config.components.slider

import io.github.notenoughupdates.moulconfig.gui.GuiComponent
import io.github.notenoughupdates.moulconfig.gui.editors.ComponentEditor
import io.github.notenoughupdates.moulconfig.observer.GetSetter
import io.github.notenoughupdates.moulconfig.processor.ProcessedOption

@Suppress("UNCHECKED_CAST")
class GuiOptionSlider : ComponentEditor {

	var component: GuiComponent

	constructor(option: ProcessedOption, minValue: Float, maxValue: Float, minStep: Float) : super(option) {

		component = wrapComponent(
			SliderComponentWithText(
				option.intoProperty() as GetSetter<Float>,
				minValue,
				maxValue,
				if (minStep < 0f) 0.01f else minStep,
			)
		)
	}

	override fun getDelegate(): GuiComponent {
		return component
	}
}