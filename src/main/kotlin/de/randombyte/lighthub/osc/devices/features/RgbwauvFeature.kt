package de.randombyte.lighthub.osc.devices.features

import de.randombyte.lighthub.osc.devices.features.colors.Rgbwauv

interface RgbwauvFeature : RgbwFeature {
    var rgbwauv: Rgbwauv
}