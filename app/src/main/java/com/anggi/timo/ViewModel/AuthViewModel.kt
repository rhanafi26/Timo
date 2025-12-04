package com.anggi.timo.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anggi.timo.Model.User
import com.anggi.timo.Repository.InMemoryRepository

class AuthViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> get() = _loginResult

    fun login(email: String, password: String) {
        val user = InMemoryRepository.login(email, password)
        _loginResult.value = user
    }
}
