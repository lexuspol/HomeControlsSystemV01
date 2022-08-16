package com.example.homecontrolssystemv01.domain.useCase

import com.example.homecontrolssystemv01.domain.model.ConnectSetting
import com.example.homecontrolssystemv01.domain.DataRepository

class DeleteMessageUseCase(
    private val repository: DataRepository,
) {
    suspend operator fun invoke(time: Long) {
        repository.deleteMessage(time)
    }
}
