package com.example.homecontrolssystemv01.domain.useCase

import com.example.homecontrolssystemv01.domain.DataRepository

class DeleteMessageUseCase(
    private val repository: DataRepository,
) {
    suspend operator fun invoke(id:Int) {
        repository.deleteMessage(id)
    }
}
