package com.anggi.timo.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.anggi.timo.laporan.LaporanBelajarEntity
import com.anggi.timo.laporan.StatistikPelajaran
import com.anggi.timo.repository.LaporanBelajarRepository
import kotlinx.coroutines.launch

class RoomBelajarViewModel(private val repository: LaporanBelajarRepository) : ViewModel() {

    fun getTotalFokusHarian(tanggal: String): LiveData<Int> = repository.getTotalFokusHarian(tanggal).asLiveData()
    fun getTotalIstirahatHarian(tanggal: String): LiveData<Int> = repository.getTotalIstirahatHarian(tanggal).asLiveData()

    fun getTotalFokusMingguan(startDate: String, endDate: String): LiveData<Int> = repository.getTotalFokusMingguan(startDate, endDate).asLiveData()
    fun getTotalIstirahatMingguan(startDate: String, endDate: String): LiveData<Int> = repository.getTotalIstirahatMingguan(startDate, endDate).asLiveData()

    fun getTotalFokusBulanan(bulan: String): LiveData<Int> = repository.getTotalFokusBulanan(bulan).asLiveData()
    fun getTotalIstirahatBulanan(bulan: String): LiveData<Int> = repository.getTotalIstirahatBulanan(bulan).asLiveData()

    fun getStatistikListHarian(tanggal: String): LiveData<List<StatistikPelajaran>> = repository.getStatistikListHarian(tanggal).asLiveData()
    fun getStatistikListMingguan(startDate: String, endDate: String): LiveData<List<StatistikPelajaran>> = repository.getStatistikListMingguan(startDate, endDate).asLiveData()
    fun getStatistikListBulanan(bulan: String): LiveData<List<StatistikPelajaran>> = repository.getStatistikListBulanan(bulan).asLiveData()

    // Fungsi eksekusi ke Room
    fun insert(laporan: LaporanBelajarEntity) = viewModelScope.launch { repository.insert(laporan) }
    fun delete(laporan: LaporanBelajarEntity) = viewModelScope.launch { repository.delete(laporan) }
}

class RoomBelajarViewModelFactory(private val repository: LaporanBelajarRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomBelajarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoomBelajarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}