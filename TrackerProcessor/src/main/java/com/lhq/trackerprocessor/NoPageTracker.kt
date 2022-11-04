package com.lhq.trackerprocessor

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * create by lihuiqiang on 2022/11/4
 *
 * exclude some class use this annotation
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(RetentionPolicy.RUNTIME)
annotation class NoPageTracker