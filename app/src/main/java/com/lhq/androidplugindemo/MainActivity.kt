package com.lhq.androidplugindemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.lhq.trackerprocessor.PageTracker
import java.io.File

class MainActivity : AppCompatActivity(), OnItemClickListener,
    com.chad.library.adapter.base.listener.OnItemClickListener, OnItemChildClickListener, OnClickListener {
    private lateinit var mBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PageTracker.init("${filesDir.absolutePath}${File.separator}tracker.txt")
        mBtn = findViewById<Button>(R.id.btn_go)


        mBtn.setOnClickListener(object : OnClickListener {
            override fun onClick(v: View?) {

            }
        })
        mBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        }
    }

    fun clickBtn(view: View) {

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
    }

    override fun onClick(v: View?) {

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}