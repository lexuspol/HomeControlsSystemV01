package com.example.homecontrolssystemv01.domain.useCase

import com.example.homecontrolssystemv01.domain.DataRepository

class PutControlUseCase (
    private val repository: DataRepository,
) {
    operator fun invoke(controlMode:Int) {
        repository.putControl(controlMode)
    }
}
