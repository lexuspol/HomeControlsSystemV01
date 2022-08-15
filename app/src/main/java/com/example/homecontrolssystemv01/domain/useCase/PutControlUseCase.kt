package com.example.homecontrolssystemv01.domain.useCase

import com.example.homecontrolssystemv01.domain.DataRepository
import com.example.homecontrolssystemv01.domain.model.ControlInfo

class PutControlUseCase (
    private val repository: DataRepository,
) {
    operator fun invoke(controlInfo: ControlInfo) {
        repository.putControl(controlInfo)
    }
}
