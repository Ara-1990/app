package com.the.app

import android.app.ActivityManager
import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}

fun TextView.showStatus(res: Int) {
    setText(res)
    visibility = View.VISIBLE
}

fun TextView.hide() {
    visibility = View.GONE
}


fun Button.customize(resText: Int, resDrawable: Int) {
    setText(resText)
    setCompoundDrawablesWithIntrinsicBounds(0, 0, resDrawable, 0)
}

fun View.onInitialized(onInit: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (isShown) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                onInit()
            }
        }
    })
}

fun serviceRunning(serviceClass: Class<*>, context: Context): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    @Suppress("DEPRECATION")
    for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}