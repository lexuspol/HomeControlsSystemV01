package com.example.homecontrolssystemv01.domain.useCase

import com.example.homecontrolssystemv01.domain.DataRepository

class LoadDataUseCase(
    private val repository: DataRepository,
) {
    operator fun invoke(mode: String,ssidSet:String) {
        repository.loadData(mode,ssidSet)
    }
}
