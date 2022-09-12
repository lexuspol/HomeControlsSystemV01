package com.example.homecontrolssystemv01.domain.useCase

import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import com.example.homecontrolssystemv01.domain.DataRepository
import com.example.homecontrolssystemv01.domain.model.DataSetting
import com.example.homecontrolssystemv01.domain.model.Message

class PutMessageUseCase (
    private val repository: DataRepository,
) {
    suspend operator fun invoke(message:Message) {
        repository.putMessage(message)
    }
}
