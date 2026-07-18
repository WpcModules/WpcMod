package net.wapic.wpcmod.util.render

import com.mojang.blaze3d.GpuFormat
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat

object CustomVertexFormat {
	val ROUNDED_RECTANGLE = VertexFormat.builder(0)
		.addAttribute(DefaultVertexFormat.POSITION_SEMANTIC_NAME, GpuFormat.RGB32_FLOAT)
		.addAttribute(DefaultVertexFormat.UV0_SEMANTIC_NAME, GpuFormat.RG32_FLOAT)
		.addAttribute("UV1", GpuFormat.RG32_FLOAT)
		.addAttribute("Roundness", GpuFormat.RGBA32_FLOAT)
		.addAttribute(DefaultVertexFormat.COLOR_SEMANTIC_NAME, GpuFormat.RGBA8_UNORM)
		.build()
}