package com.hellcorp.locationtrackerapp.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.hellcorp.locationtrackerapp.R

class SettingsFragment : PreferenceFragmentCompat() {
    private var timePref: Preference? = null
    private var colorPref: Preference? = null
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey)
        init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timePref = null
        colorPref = null
    }

    private fun init() {
        timePref = findPreference(TIME_KEY)
        colorPref = findPreference(COLOR_TRACK_KEY)

        val changeSiltener = onChangeListener()
        timePref!!.onPreferenceChangeListener = changeSiltener
        colorPref!!.onPreferenceChangeListener = changeSiltener
        initPrefs()
    }

    private fun onChangeListener(): OnPreferenceChangeListener {
        return OnPreferenceChangeListener { preference, newValue ->
            when (preference.key) {
                TIME_KEY -> onTimeChange(newValue.toString())
                COLOR_TRACK_KEY -> preference.icon?.setTint(Color.parseColor(newValue.toString()))
            }
            true
        }
    }

    private fun onTimeChange(newValue: String) {
        val nameArray = resources.getStringArray(R.array.loc_time_update_name)
        val valueArray = resources.getStringArray(R.array.loc_time_update_value)
        val title = timePref?.title.toString().substringBefore(":")
        timePref?.title = "$title: ${nameArray[valueArray.indexOf(newValue)]}"
    }

    private fun initPrefs() {
        val preference = timePref!!.preferenceManager.sharedPreferences
        val nameArray = resources.getStringArray(R.array.loc_time_update_name)
        val valueArray = resources.getStringArray(R.array.loc_time_update_value)
        val title = timePref!!.title
        timePref!!.title = "$title: ${
            nameArray[valueArray.indexOf(
                preference?.getString(
                    TIME_KEY,
                    UPDATE_TIME_3_SEC
                )
            )]
        }"

        val trackColor = preference?.getString(COLOR_TRACK_KEY, DEFAULT_COLOR)
        colorPref?.icon?.setTint(Color.parseColor(trackColor))
    }

    companion object {
        const val TIME_KEY = "update_time_key"
        const val COLOR_TRACK_KEY = "track_color_key"
        private const val DEFAULT_COLOR = "#2962FF"
        private const val UPDATE_TIME_3_SEC = "3000"
    }
}
