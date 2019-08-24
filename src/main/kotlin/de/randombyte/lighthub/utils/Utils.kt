package de.randombyte.lighthub.utils

// this function exists because for some reason the kotlin compiler can't infer the type when a generic is used
fun <T> flatten(vararg objects: List<T>) = objects.toList().flatten()