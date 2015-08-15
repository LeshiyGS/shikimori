#include <jni.h>

JNIEXPORT jobject JNICALL
Java_org_shikimori_library_tool_parser_jsop_BodyBuild_parceNative(JNIEnv *env, jobject instance,
                                                                  jstring text_, jobject viewBody) {
    const char *text = (*env)->GetStringUTFChars(env, text_, 0);
    jmethodID methodID = env->GetMethodID(cls, "parce", "(Ljava/lang/String;)V");
    // TODO

    (*env)->ReleaseStringUTFChars(env, text_, text);
}