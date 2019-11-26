package de.randombyte.lighthub.config

class GeneralConfig(
    val `beats-per-minute`: Int = 120,
    val `ticks-per-second`: Int = 30,
    val `bpm-fader-min`: Int = 70,
    val `bpm-fader-max`: Int = 140,
    val `transition-ticks-fader-min`: Int = 1,
    val `transition-ticks-fader-max`: Int = 90
)