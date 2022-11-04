package com.lhq.androidplugindemo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lhq.trackerprocessor.PageTracker


/**
 * A simple [Fragment] subclass.
 * Use the [AFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("tracker", "onCreate")
        PageTracker.writeLog("onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_a, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("tracker", "onDestroy")
        PageTracker.writeLog("onDestroy")
    }
}