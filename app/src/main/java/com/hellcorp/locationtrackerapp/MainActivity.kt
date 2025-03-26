package com.hellcorp.locationtrackerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hellcorp.locationtrackerapp.databinding.ActivityMainBinding
import com.hellcorp.locationtrackerapp.fragments.MainFragment
import com.hellcorp.locationtrackerapp.fragments.SettingsFragment
import com.hellcorp.locationtrackerapp.fragments.TrackListFragment
import com.hellcorp.locationtrackerapp.utils.applyBlurEffect
import com.hellcorp.locationtrackerapp.utils.clearBlurEffect
import com.hellcorp.locationtrackerapp.utils.openFragment

class MainActivity : AppCompatActivity(), MainActivityBlur {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        provideClickListeners()
        openFragment(MainFragment.newInstance())
    }

    private fun provideClickListeners() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_home -> openFragment(MainFragment.newInstance())
                R.id.item_tracks -> openFragment(TrackListFragment.newInstance())
                R.id.item_settings -> openFragment(SettingsFragment())
            }
            true
        }
    }

    override fun applyBlurEffect() {
        binding.bottomNavigationView.applyBlurEffect()
    }

    override fun clearBlurEffect() {
        binding.bottomNavigationView.clearBlurEffect()
    }
}