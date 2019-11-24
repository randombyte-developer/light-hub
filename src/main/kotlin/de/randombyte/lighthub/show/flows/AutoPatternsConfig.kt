package de.randombyte.lighthub.show.flows

abstract class AutoPatternsConfig(
    val interval: Int = 8,
    val `device-type-offset`: Int = 0,
    val `global-type-offset`: Int = 0
)