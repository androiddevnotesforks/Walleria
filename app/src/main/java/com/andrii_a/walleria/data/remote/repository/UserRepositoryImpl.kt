package com.andrii_a.walleria.data.remote.repository

import com.andrii_a.walleria.data.remote.services.UserService
import com.andrii_a.walleria.domain.network.Resource
import com.andrii_a.walleria.data.util.network.backendRequestFlow
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(private val userService: UserService) : UserRepository {

    override fun getUserPublicProfile(username: String): Flow<Resource<User>> =
        backendRequestFlow {
            userService.getUserPublicProfile(username).toUser()
        }
}