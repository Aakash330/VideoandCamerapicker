package com.aakash.imageandvideopicker.videofilter.model

import android.graphics.Color
import com.aakash.imageandvideopicker.videofilter.*
import com.aakash.imageandvideopicker.videofilter.filter.AutoFixFilter
import com.aakash.imageandvideopicker.videofilter.filter.GrainFilter
import com.aakash.imageandvideopicker.videofilter.filter.HueFilter


// TODO: Rewite to factory
class Shaders(width: Int, height: Int) {

    companion object {
        private const val SUFFIX = "Effect"
    }

    private val shaders = arrayOf(
            // Filters
            AutoFixFilter(),
            GrainFilter(width, height),
            HueFilter(),

            // Effects
            AutoFixEffect(0.0F),
            BlackAndWhiteEffect(),
            BrightnessEffect(0.5F),
            ContrastEffect(0.5F),
            CrossProcessEffect(),
            DocumentaryEffect(),
            DuotoneEffect(),
            FillLightEffect(0.5F),
            GammaEffect(1.0F),
            GreyScaleEffect(),
            InvertColorsEffect(),
            LamoishEffect(),
            PosterizeEffect(),
            SaturationEffect(0.5F),
            SepiaEffect(),
            SharpnessEffect(0.5F),
            TemperatureEffect(0.5F),
            TintEffect(Color.GREEN),
            VignetteEffect(0F)
    )

    val count = shaders.size

    fun getShader(index: Int) = shaders[index]

    fun getShaderName(index: Int) = shaders[index]::class.java.simpleName.removeSuffix(SUFFIX)
}