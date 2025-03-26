package com.hellcorp.locationtrackerapp.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.hellcorp.locationtrackerapp.MainActivityBlur
import com.hellcorp.locationtrackerapp.databinding.SaveDialogBinding
import com.hellcorp.locationtrackerapp.domain.TrackItem

object DialogManager {
    fun showLocationEnableDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("GPS sensor is currently desabled, you should to switch it on to use thr app.")
        dialog.setMessage("Enable GPS?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes") { _, _ ->
            listener.onClick()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") { _, _ -> }
        dialog.show()
    }

    fun showSaveTrackDialog(context: Context, track: TrackItem?, parent: View, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val binding = SaveDialogBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val dialog = builder.create()
        binding.apply {
            tvTime.text = track?.time
            tvAverageSpeed.text = track?.averageSpeed
            tvDistance.text = track?.distance
            btnSave.setOnClickListener {
                listener.onClick()
                clearBlur(context, parent)
                dialog.dismiss()
            }
            btnDiscard.setOnClickListener {
                clearBlur(context, parent)
                dialog.dismiss()
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setOnCancelListener {
            clearBlur(context, parent)
        }
        applyBlur(context, parent)
        dialog.show()
    }

    private fun applyBlur(context: Context, parent: View) {
        parent.applyBlurEffect()
        (context as? MainActivityBlur)?.applyBlurEffect()
    }

    private fun clearBlur(context: Context, parent: View) {
        parent.clearBlurEffect()
        (context as? MainActivityBlur)?.clearBlurEffect()
    }

    interface Listener {
        fun onClick()
    }
}