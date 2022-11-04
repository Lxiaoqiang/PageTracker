package com.lhq.tracker

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

/**
 * create by lihuiqiang on 2022/10/31
 *
 */
interface TrackerParameters: InstrumentationParameters {

    @get:Input
    var filterPackage: String?

}