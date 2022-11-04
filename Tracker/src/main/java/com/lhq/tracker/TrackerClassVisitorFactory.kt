package com.lhq.tracker

import com.android.build.api.instrumentation.*
import com.lhq.page_tracker.PageTrackerVisitor
import org.objectweb.asm.ClassVisitor

/**
 * create by lihuiqiang on 2022/10/31
 *
 */
abstract class TrackerClassVisitorFactory: AsmClassVisitorFactory<TrackerParameters> {

//    private val TAG = "TrackerClassVisitorFactory"

    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return PageTrackerVisitor(nextClassVisitor, classContext.currentClassData)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val param = parameters.get().filterPackage
        val isInFilterPackage = if (param.isNullOrEmpty()) {
            true
        } else {
            classData.className.contains(param.replace("*", ""))
        }
        val isActOrFrag = (classData.superClasses.contains("android.app.Activity") || classData.superClasses.contains("androidx.fragment.app.Fragment"))

        val isAndroidFile = classData.className.contains("R$")
                || classData.className.contains("BuildConfig")

        val hasNoTackerFlag = classData.classAnnotations.contains("com.lhq.trackerprocessor.NoPageTracker")
        return isInFilterPackage
                && !isAndroidFile
                && isActOrFrag
                && !hasNoTackerFlag
    }
}