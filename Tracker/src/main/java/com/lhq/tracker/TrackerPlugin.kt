package com.lhq.tracker

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * create by lihuiqiang on 2022/10/31
 *
 */
abstract class TrackerPlugin: Plugin<Project> {
    override fun apply(project: Project) {

        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        println("androidComponents: $androidComponents")
        val tp = project.extensions.create("trackerExt", TrackerParameters::class.java)
        androidComponents.onVariants { variant ->
            println("variant: $variant")
            variant.transformClassesWith(TrackerClassVisitorFactory::class.java,
                InstrumentationScope.ALL) {
                it.filterPackage = tp.filterPackage
            }
            variant.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
        }
    }
}