package com.example.homecontrolssystemv01.domain.useCase

import com.example.homecontrolssystemv01.domain.DataRepository

class GetDataListUseCase(
    private val repository: DataRepository
) {
    //нет смыса давать такое имя методу, переопределяют оператор invoke и тепеть юскейс можно вызывать как метод
    operator fun invoke() = repository.getDataList()
}