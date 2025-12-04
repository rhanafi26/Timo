package com.anggi.timo.Repository

import com.anggi.timo.Model.*

object InMemoryRepository {
    private val users = mutableListOf<User>()
    private val types = mutableListOf<TypeStudy>()
    private val timeStudies = mutableListOf<TimeStudy>()

    init {
        users.add(User(1, "test", "tes@gmail.com", "1234"))
    }

    fun login(email: String, password: String): User? {
        return users.find { it.email == email && it.password == password }
    }
    fun addUser(user: User) = users.add(user)
    fun getUsers(): List<User> = users

    fun addType(type: TypeStudy) = types.add(type)
    fun getTypes(): List<TypeStudy> = types

    fun addTimeStudy(timeStudy: TimeStudy) = timeStudies.add(timeStudy)
    fun getTimeStudies(): List<TimeStudy> = timeStudies

    fun getTimeStudiesByUser(userId: Int): List<TimeStudy> =
        timeStudies.filter { it.userId == userId }


}
