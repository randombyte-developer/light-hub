package de.randombyte.lighthub.utils

fun ULong.rem(other: Int) = rem(other.toULong())

fun ULong.multipleOf(other: Int) = rem(other) == 0uL