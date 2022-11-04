package com.lhq.androidplugindemo

import com.google.gson.Gson
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val str = "{\"name\":null, \"sex\": 1}"
        val bean = Gson().fromJson(str, User::class.java)
        print(bean)
    }


    data class User(
        val name: String? = "",
        val sex: Int = 0
    )
}