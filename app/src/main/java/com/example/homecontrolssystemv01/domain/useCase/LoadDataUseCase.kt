package com.example.homecontrolssystemv01.domain.useCase

import com.example.homecontrolssystemv01.data.ConnectSetting
import com.example.homecontrolssystemv01.domain.DataRepository

class LoadDataUseCase(
    private val repository: DataRepository,
) {
    operator fun invoke(connectSetting: ConnectSetting) {
        repository.loadData(connectSetting)
    }
}
