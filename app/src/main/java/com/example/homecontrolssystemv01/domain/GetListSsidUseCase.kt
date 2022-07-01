package com.example.homecontrolssystemv01.domain

class GetListSsidUseCase (
    private val repository: DataRepository
) {
    operator fun invoke() = repository.getSsidList()
}




