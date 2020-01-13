package com.project.distance.ext

inline fun <T> List<T>.permute(): List<Pair<T, T>> {
    val result = mutableListOf<Pair<T, T>>()
    (0 until size).forEach { i ->
        ((i + 1) until size).forEach { j ->
            result.add(get(i) to get(j))
        }
    }
    return result
}