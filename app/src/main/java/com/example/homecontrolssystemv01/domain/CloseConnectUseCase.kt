package com.example.homecontrolssystemv01.domain

class CloseConnectUseCase (
    private val repository: DataRepository,
) {
    operator fun invoke() {
        repository.closeConnect()
    }
}
