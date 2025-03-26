package com.hellcorp.locationtrackerapp.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.hellcorp.locationtrackerapp.R

fun Fragment.openFragment(f: Fragment) {
    (activity as AppCompatActivity).supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.placeHolder, f).commit()
}

fun AppCompatActivity.openFragment(f: Fragment) {
    if (supportFragmentManager.fragments.isEmpty() || supportFragmentManager.fragments[0].javaClass != f.javaClass) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.placeHolder, f).commit()
    }
}

fun Fragment.checkPermission(permission: String): Boolean {
    return when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(requireActivity(), permission) -> true
        else -> false
    }
}

fun showSnackbar(
    view: View,
    message: String,
    context: Context
) {
    val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
    val snackTextColor = ContextCompat.getColor(context, R.color.white)
    val backgroundColor = ContextCompat.getColor(context, R.color.text_color)

    val textView =
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
    textView.textSize = 16f
    textView.setTextColor(snackTextColor)
    snackbar.view.setBackgroundColor(backgroundColor)
    snackbar.show()
}

fun View.applyBlurEffect(radius: Float = 15f, tileMode: Shader.TileMode = Shader.TileMode.MIRROR) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val renderEffect = RenderEffect.createBlurEffect(radius, radius, tileMode)
        this.setRenderEffect(renderEffect)
    }
}

fun View.clearBlurEffect() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.setRenderEffect(null)
    }
}
