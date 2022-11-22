package com.example.homecontrolssystemv01.domain.useCase

import com.example.homecontrolssystemv01.domain.DataRepository
import com.example.homecontrolssystemv01.domain.model.setting.DataSetting

class PutSettingUseCase (
    private val repository: DataRepository,
) {
    suspend operator fun invoke(dataSetting: DataSetting) {
        repository.putDataSetting(dataSetting)
    }
}
