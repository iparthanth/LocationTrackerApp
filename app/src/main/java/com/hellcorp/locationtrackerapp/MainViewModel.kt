package com.hellcorp.locationtrackerapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hellcorp.locationtrackerapp.data.ConverterDB
import com.hellcorp.locationtrackerapp.data.MainDB
import com.hellcorp.locationtrackerapp.data.db.TrackItemEntity
import com.hellcorp.locationtrackerapp.domain.TrackItem
import com.hellcorp.locationtrackerapp.location.LocationModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class MainViewModel(db: MainDB, converterDB: ConverterDB): ViewModel() {
    val trackDAO = db.getDao()
    val converter = converterDB
    val currentTrack = MutableLiveData<TrackItem>()
    val locationUpdates = MutableLiveData<LocationModel>()
    val timeData = MutableLiveData<String>()
    val trackList = trackDAO.getTrackList().map {list ->
        list.map {
            converterDB.map(it)
        }
    }.asLiveData()

    class VMFactory(private val db: MainDB, private val converterDb: ConverterDB) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(db, converterDb) as T
            }
            throw  IllegalArgumentException("MainViewModel: Unknow ViewModel class")
        }
    }

    fun saveTrackToDb(trackItem: TrackItem) {
        viewModelScope.launch {
            trackDAO.insertTrack(converter.map(trackItem))
        }
    }

    fun removeTrackFromDb(trackItem: TrackItem) {
        viewModelScope.launch {
            trackItem.id?.let { trackDAO.removeTrack(it) }
        }
    }

    fun setCurreantTrack(trackItem: TrackItem) {
        currentTrack.value = trackItem
    }
}