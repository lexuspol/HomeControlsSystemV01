package com.example.homecontrolssystemv01

import org.junit.Test

import org.junit.Assert.*
import com.example.homecontrolssystemv01.data.mapper.DataMapper
import com.example.homecontrolssystemv01.util.convertLongToTime
import com.example.homecontrolssystemv01.util.convertStringTimeToLong

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        println(convertLongToTime(20))
        println(convertStringTimeToLong("1970-01-01 03:00:00","yyyy-MM-dd HH:mm:ss"))


        assertEquals(5, 3 + 2)
    }


}