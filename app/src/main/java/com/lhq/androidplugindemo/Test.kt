package com.lhq.androidplugindemo

import android.util.Log
import android.view.View
import android.view.View.OnClickListener

/**
 * create by lihuiqiang on 2022/11/2
 *
 */
class Test {

    fun init(path: String) {

    }

    fun test() {
        val view: View? = null
        view?.setOnClickListener(object :OnClickListener{
            override fun onClick(v: View?) {

            }
        })
        view?.setOnClickListener {

        }

    }
}