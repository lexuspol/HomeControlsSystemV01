package com.example.homecontrolssystemv01.domain.useCase

import com.example.homecontrolssystemv01.domain.DataRepository
import com.example.homecontrolssystemv01.domain.model.Message

class PutMessageUseCase (
    private val repository: DataRepository,
) {
    suspend operator fun invoke(message:Message) {
        repository.putMessage(message)
    }
}
