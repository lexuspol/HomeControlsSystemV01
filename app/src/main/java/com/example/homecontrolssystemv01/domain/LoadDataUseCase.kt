package com.example.homecontrolssystemv01.domain

import android.util.Log

class LoadDataUseCase(
    private val repository: DataRepository,
) {
    operator fun invoke(parameters: Parameters) {
        repository.loadData(parameters)
    }
}
