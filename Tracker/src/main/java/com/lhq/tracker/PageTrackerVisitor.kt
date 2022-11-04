package com.lhq.page_tracker

import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.commons.AdviceAdapter


/**
 * create by lihuiqiang on 2022/10/31
 *
 */
class PageTrackerVisitor(nextVisitor: ClassVisitor?, classData: ClassData?) : ClassVisitor(Opcodes.ASM7, nextVisitor) {

    private var insertedOnCreate = false
    private var insertedOnDestroy = false
    private var mClassName: String? = null
    private val mClassVisitor = nextVisitor
    private val mCurrentClassData = classData

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        println("mCurrentClassData: $mCurrentClassData")
        mClassName = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitSource(source: String?, debug: String?) {
        super.visitSource(source, debug)
    }


    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)

        val isOnCreateMethod = name == "onCreate" && descriptor == "(Landroid/os/Bundle;)V"
        val isOnDestroyMethod = name == "onDestroy" && descriptor == "()V"
        println("isOnCreateMethod: $isOnCreateMethod, isOnDestroyMethod: $isOnDestroyMethod")
        return if (isOnCreateMethod) {
            println("OnCreateAdviceAdapter")
            insertedOnCreate = true
            OnCreateAdviceAdapter(mClassName, access, name, descriptor, methodVisitor)
        } else if (isOnDestroyMethod) {
            println("OnDestroyAdviceAdapter")
            insertedOnDestroy = true
            OnDestroyAdviceAdapter(mClassName, access, name, descriptor, methodVisitor)
        }else {
            methodVisitor
        }
    }

    override fun visitEnd() {
        println("insertedOnCreate: $insertedOnCreate, insertedOnDestroy: $insertedOnDestroy")
        mCurrentClassData?.let { classData ->
            if (!insertedOnCreate) {
                if (classData.superClasses.contains("android.app.Activity")) {
                    insertActivityOnCreateCode(mCurrentClassData.className, mCurrentClassData.superClasses[0])
                }
                if (classData.superClasses.contains("androidx.fragment.app.Fragment")) {
                    insertFragmentOnCreateCode(mCurrentClassData.superClasses[0])
                }
            }

            if (!insertedOnDestroy) {
                if (classData.superClasses.contains("android.app.Activity")) {
                    insertActivityOnDestroyCode(mCurrentClassData.superClasses[0])
                }
                if (classData.superClasses.contains("androidx.fragment.app.Fragment")) {
                    insertFragmentOnDestroyCode(mCurrentClassData.superClasses[0])
                }
            }
        }
        super.visitEnd()
    }

    private fun insertActivityOnCreateCode(className: String, superClassName: String) {
        println("[insertActivityOnCreateCode] className: $className, superClassName: $superClassName, ${superClassName.replace(".", "/")}")
        //create OnCreate method
        val methodVisitor = mClassVisitor?.visitMethod(ACC_PROTECTED, "onCreate", "(Landroid/os/Bundle;)V", null, null);
        methodVisitor?.visitAnnotableParameterCount(1, false);
        methodVisitor?.visitCode();
        methodVisitor?.visitVarInsn(ALOAD, 0);
        methodVisitor?.visitVarInsn(ALOAD, 1);
        methodVisitor?.visitMethodInsn(INVOKESPECIAL, superClassName.replace(".","/"), "onCreate", "(Landroid/os/Bundle;)V", false);
        methodVisitor?.visitVarInsn(ALOAD, 0);
        methodVisitor?.visitLdcInsn( Integer(2131427356));
        methodVisitor?.visitMethodInsn(INVOKEVIRTUAL, className.replace(".", "/"), "setContentView", "(I)V", false);
        //logcat
        methodVisitor?.visitLdcInsn("page_tracker");
        methodVisitor?.visitLdcInsn("$mClassName------> onCreate\n");
        methodVisitor?.visitMethodInsn(INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        methodVisitor?.visitInsn(POP);
        //logfile
        methodVisitor?.visitFieldInsn(GETSTATIC, "com/lhq/trackerprocessor/PageTracker", "INSTANCE", "Lcom/lhq/trackerprocessor/PageTracker;");
        methodVisitor?.visitLdcInsn("${mClassName}: onCreate\n");
        methodVisitor?.visitMethodInsn(INVOKEVIRTUAL, "com/lhq/trackerprocessor/PageTracker", "writeLog", "(Ljava/lang/String;)V", false);
        methodVisitor?.visitInsn(RETURN);
        methodVisitor?.visitMaxs(2, 2);
        methodVisitor?.visitEnd();
        mClassVisitor?.visitEnd()
    }

    private fun insertActivityOnDestroyCode(superClassName: String) {
        println("[insertActivityOnDestroyCode] superClassName: $superClassName, ${superClassName.replace(".", "/")}")
        val methodVisitor = mClassVisitor?.visitMethod(ACC_PROTECTED, "onDestroy", "()V", null, null);
        methodVisitor?.visitCode();
        methodVisitor?.visitVarInsn(ALOAD, 0);
        methodVisitor?.visitMethodInsn(INVOKESPECIAL, superClassName.replace(".", "/"), "onDestroy", "()V", false);
        //logcat
        methodVisitor?.visitLdcInsn("page_tracker");
        methodVisitor?.visitLdcInsn("$mClassName------> onDestroy\n");
        methodVisitor?.visitMethodInsn(INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        methodVisitor?.visitInsn(POP);

        methodVisitor?.visitFieldInsn(GETSTATIC, "com/lhq/trackerprocessor/PageTracker", "INSTANCE", "Lcom/lhq/trackerprocessor/PageTracker;");
        methodVisitor?.visitLdcInsn("${mClassName}: onDestroy\n");
        methodVisitor?.visitMethodInsn(INVOKEVIRTUAL, "com/lhq/trackerprocessor/PageTracker", "writeLog", "(Ljava/lang/String;)V", false);
        methodVisitor?.visitInsn(RETURN);
        methodVisitor?.visitMaxs(2, 1);
        methodVisitor?.visitEnd();
        mClassVisitor?.visitEnd()
    }

    private fun insertFragmentOnCreateCode(superClassName: String) {
        println("[insertFragmentOnCreateCode] superClassName: $superClassName, ${superClassName.replace(".", "/")}")
        val methodVisitor = mClassVisitor?.visitMethod(ACC_PUBLIC, "onCreate", "(Landroid/os/Bundle;)V", null, null);
        methodVisitor?.visitAnnotableParameterCount(1, false);
        methodVisitor?.visitCode();
        methodVisitor?.visitVarInsn(ALOAD, 0);
        methodVisitor?.visitVarInsn(ALOAD, 1);
        methodVisitor?.visitMethodInsn(INVOKESPECIAL, superClassName.replace(".", "/"), "onCreate", "(Landroid/os/Bundle;)V", false);
        //logcat
        methodVisitor?.visitLdcInsn("page_tracker");
        methodVisitor?.visitLdcInsn("${mClassName}: onCreate");
        methodVisitor?.visitMethodInsn(INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        methodVisitor?.visitInsn(POP);
        //logfile
        methodVisitor?.visitFieldInsn(GETSTATIC, "com/lhq/trackerprocessor/PageTracker", "INSTANCE", "Lcom/lhq/trackerprocessor/PageTracker;");
        methodVisitor?.visitLdcInsn("${mClassName}: onCreate\n");
        methodVisitor?.visitMethodInsn(INVOKEVIRTUAL, "com/lhq/trackerprocessor/PageTracker", "writeLog", "(Ljava/lang/String;)V", false);
        methodVisitor?.visitInsn(RETURN);
        methodVisitor?.visitMaxs(2, 2);
        methodVisitor?.visitEnd();
    }

    private fun insertFragmentOnDestroyCode(superClassName: String) {
        println("[insertFragmentOnDestroyCode] superClassName: $superClassName, ${superClassName.replace(".", "/")}")
        val methodVisitor = mClassVisitor?.visitMethod(ACC_PUBLIC, "onDestroy", "()V", null, null);
        methodVisitor?.visitCode();
        methodVisitor?.visitVarInsn(ALOAD, 0);
        methodVisitor?.visitMethodInsn(INVOKESPECIAL, superClassName.replace(".", "/"), "onDestroy", "()V", false);
        //logcat
        methodVisitor?.visitLdcInsn("page_tracker");
        methodVisitor?.visitLdcInsn("${mClassName}: onDestroy");
        methodVisitor?.visitMethodInsn(INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        methodVisitor?.visitInsn(POP);
        //logfile
        methodVisitor?.visitFieldInsn(GETSTATIC, "com/lhq/trackerprocessor/PageTracker", "INSTANCE", "Lcom/lhq/trackerprocessor/PageTracker;");
        methodVisitor?.visitLdcInsn("${mClassName}: onDestroy\n");
        methodVisitor?.visitMethodInsn(INVOKEVIRTUAL, "com/lhq/trackerprocessor/PageTracker", "writeLog", "(Ljava/lang/String;)V", false);
        methodVisitor?.visitInsn(RETURN);
        methodVisitor?.visitMaxs(2, 1);
        methodVisitor?.visitEnd();
    }

    class OnCreateAdviceAdapter(
        className: String?,
        access: Int,
        name: String?,
        descriptor: String?,
        methodVisitor: MethodVisitor?,
    ): AdviceAdapter(Opcodes.ASM7, methodVisitor, access, name, descriptor) {
        private val mClassName = className
        private val mMethodVisitor = methodVisitor
        override fun onMethodEnter() {
            //logcat
            mMethodVisitor?.visitLdcInsn("page_tracker");
            mMethodVisitor?.visitLdcInsn("${mClassName}---->onCreate");
            mMethodVisitor?.visitMethodInsn(INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mMethodVisitor?.visitInsn(POP);
            mMethodVisitor?.visitFieldInsn(GETSTATIC, "com/lhq/trackerprocessor/PageTracker", "INSTANCE", "Lcom/lhq/trackerprocessor/PageTracker;");
            mMethodVisitor?.visitLdcInsn("${mClassName}: onCreate\n");
            mMethodVisitor?.visitMethodInsn(INVOKEVIRTUAL, "com/lhq/trackerprocessor/PageTracker", "writeLog", "(Ljava/lang/String;)V", false);
        }
    }


    class OnDestroyAdviceAdapter(
        className: String?,
        access: Int,
        name: String?,
        descriptor: String?,
        methodVisitor: MethodVisitor?,
    ): AdviceAdapter(Opcodes.ASM7, methodVisitor, access, name, descriptor) {

        private val mMethodVisitor = methodVisitor
        private val mClassName = className
        override fun onMethodEnter() {
            //logcat
            mMethodVisitor?.visitLdcInsn("page_tracker");
            mMethodVisitor?.visitLdcInsn("${mClassName}---->onDestroy");
            mMethodVisitor?.visitMethodInsn(INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mMethodVisitor?.visitInsn(POP);
            mMethodVisitor?.visitFieldInsn(GETSTATIC, "com/lhq/trackerprocessor/PageTracker", "INSTANCE", "Lcom/lhq/trackerprocessor/PageTracker;");
            mMethodVisitor?.visitLdcInsn("${mClassName}: onDestroy\n");
            mMethodVisitor?.visitMethodInsn(INVOKEVIRTUAL, "com/lhq/trackerprocessor/PageTracker", "writeLog", "(Ljava/lang/String;)V", false);
        }
    }
}