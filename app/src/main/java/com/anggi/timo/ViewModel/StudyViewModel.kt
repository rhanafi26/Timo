package com.anggi.timo.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anggi.timo.Model.TimeStudy
import com.anggi.timo.Repository.InMemoryRepository

class StudyViewModel : ViewModel() {
    private val _timeStudies = MutableLiveData<List<TimeStudy>>()
    val timeStudies: LiveData<List<TimeStudy>> get() = _timeStudies

    fun loadTimeStudies(userId: Int) {
        _timeStudies.value = InMemoryRepository.getTimeStudiesByUser(userId)
    }

    fun insertTimeStudy(time: Int, userId: Int, typeId: Int) {
        val item = TimeStudy(
            id = System.currentTimeMillis().toInt(),
            time = time,
            created = System.currentTimeMillis(),
            userId = userId,
            typeStudyId = typeId
        )
        InMemoryRepository.addTimeStudy(item)
        loadTimeStudies(userId)
    }
}
