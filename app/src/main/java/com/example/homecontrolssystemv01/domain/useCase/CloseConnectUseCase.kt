package com.example.homecontrolssystemv01.domain.useCase

import com.example.homecontrolssystemv01.domain.DataRepository

class CloseConnectUseCase (
    private val repository: DataRepository,
) {
    operator fun invoke() {
        repository.closeConnect()
    }
}
