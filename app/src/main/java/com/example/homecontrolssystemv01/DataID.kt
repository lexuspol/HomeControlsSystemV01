package com.example.homecontrolssystemv01

enum class DataID(val id:Int) {


    // присваеваем имена к ID
    deviceInfo(-4),
    SSID(-3),
    connectMode(-2),

    lastTimeUpdate(-1),


    meterWater(200),
    meterElectricity(201),

    lightSleepState(511),
    lightChildState(512),
    lightCinemaState(513),
    lightOutdoorState(515),

    buttonLightSleep(-101),
    buttonLightChild(-102),
    buttonLightCinema(-103),
    buttonLightOutdoor(-104),

    garageGateOpen(503),
    slidingGateOpen(504),
    wicketUnlock(505),

    buttonWicketUnlock(-111),
    buttonGateGarageSBS(-112),
    buttonGateSlidingSBS(-113),

    slidingGateRunOpen(531),
    slidingGateRunClose(532),

    buttonSoundOff(-121),
    stateSoundOff(509),

    completeUpdate(1000),

    mainDeviceName(400),

    alarmMessage(601)

}






