@file:JvmName("ViewUtils")

package com.project.distance.ext

import android.view.View

inline fun View.show() {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

inline fun View.gone() {
    if (visibility != View.GONE) visibility = View.GONE
}
