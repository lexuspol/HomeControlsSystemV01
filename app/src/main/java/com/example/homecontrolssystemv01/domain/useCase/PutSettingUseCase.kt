package com.example.homecontrolssystemv01.domain.useCase

import com.example.homecontrolssystemv01.domain.DataRepository
import com.example.homecontrolssystemv01.domain.model.DataSetting

class PutSettingUseCase (
    private val repository: DataRepository,
) {
    operator fun invoke(dataSetting:DataSetting) {
        repository.putDataSetting(dataSetting)
    }
}
