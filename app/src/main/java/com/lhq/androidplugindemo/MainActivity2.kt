package com.lhq.androidplugindemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lhq.trackerprocessor.NoPageTracker

@NoPageTracker
class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val fragment = AFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.ll_root2, fragment, "AFragment")
            .show(fragment)
            .commit()
    }
}