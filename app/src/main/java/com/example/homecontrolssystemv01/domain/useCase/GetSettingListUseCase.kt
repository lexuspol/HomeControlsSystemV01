package com.example.homecontrolssystemv01.domain.useCase

import com.example.homecontrolssystemv01.domain.DataRepository

class GetSettingListUseCase(
    private val repository: DataRepository
) {
    operator fun invoke() = repository.getDataSetting()
}