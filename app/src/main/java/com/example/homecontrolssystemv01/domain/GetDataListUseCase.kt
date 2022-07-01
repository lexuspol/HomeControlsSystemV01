package com.example.homecontrolssystemv01.domain

class GetDataListUseCase(
    private val repository: DataRepository
) {
    operator fun invoke() = repository.getDataList()
}