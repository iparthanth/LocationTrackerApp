package com.hellcorp.locationtrackerapp

import android.app.Application
import com.hellcorp.locationtrackerapp.data.MainDB

class App : Application() {
    val database by lazy { MainDB.getDatabase(this) }
}