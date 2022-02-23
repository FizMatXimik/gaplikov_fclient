#include <jni.h>
#include <string>
#include <android/log.h>
#include <spdlog/spdlog.h>
#include <spdlog/sinks/android_sink.h>
#include <mbedtls/entropy.h>
#include <mbedtls/ctr_drbg.h>
#include <mbedtls/des.h>


mbedtls_entropy_context entropy;
mbedtls_ctr_drbg_context ctr_drbg;
char *personalization = "gaplikov_fclient-sample-app";

#define LOG_INFO(...) __android_log_print(ANDROID_LOG_INFO, "gaplikov_fclient_ndk", __VA_ARGS__ )
#define SLOG_INFO(...) android_logger->info(__VA_ARGS__)
auto android_logger = spdlog::android_logger_mt("android", "gaplikov_fclient_ndk");

extern "C" JNIEXPORT jstring JNICALL
Java_ru_igap_gaplikov_1fclient_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    LOG_INFO("Hello from c++ %d", 2022);
    SLOG_INFO("Hello from spdlog {0}", 2022);
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jint JNICALL
Java_ru_igap_gaplikov_1fclient_MainActivity_initRng(JNIEnv *env, jclass clazz) {
    mbedtls_entropy_init( &entropy );
    mbedtls_ctr_drbg_init( &ctr_drbg );

    return mbedtls_ctr_drbg_seed( &ctr_drbg , mbedtls_entropy_func, &entropy,
                                  (const unsigned char *) personalization,
                                  strlen( personalization ) ); // Создаем сид для случайной генерации
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_ru_igap_gaplikov_1fclient_MainActivity_randomBytes(JNIEnv *env, jclass clazz, jint no) {
    uint8_t * buf = new uint8_t [no];
    mbedtls_ctr_drbg_random(&ctr_drbg, buf, no);
    jbyteArray rnd = env->NewByteArray(no);
    env->SetByteArrayRegion(rnd, 0, no, (jbyte *)buf);
    delete[] buf;
    return rnd;
}
extern "C"
JNIEXPORT jbyteArray JNICALL
Java_ru_igap_gaplikov_1fclient_MainActivity_encrypt(JNIEnv *env, jclass clazz, jbyteArray key,
                                                    jbyteArray data) {
    jsize ksz = env->GetArrayLength(key);
    jsize dsz = env->GetArrayLength(data);
    if ((ksz != 16) || (dsz <= 0)) {
        return env->NewByteArray(0);
    }
    mbedtls_des3_context ctx;
    mbedtls_des3_init(&ctx); // инициализация структуры контекста

    jbyte * pkey = env->GetByteArrayElements(key, 0);

    //padding PKCS#5
    // Заполнение целыми байтами. Значением каждого добавленного байта является
    // число добавляемых байтов, т.е. добавляется N байтов,
    // каждый из которых имеет значение N. Количество добавляемых байтов
    // будет зависеть от границы блока, до которого необходимо расширить сообщение.

    int rst = dsz % 8;
    int sz = dsz + 8 - rst; // Делаем размер кратный блокам по 8 байт
    uint8_t * buf = new uint8_t[sz]; // Создаем буфер размером sz
    for (int i = 7; i > rst; i--) { // Проходимся по добавляемым в конец байтам
        buf[dsz + i] = rst; // добавляем байт со значением общего количества добавляемых байтов
    }

    jbyte * pdata = env->GetByteArrayElements(data, 0);
    std::copy(pdata, pdata + dsz, buf);
    mbedtls_des3_set2key_enc(&ctx, (uint8_t *)pkey); //Установка секретного ключа для шифрования в контекст
    int cn = sz / 8;
    for (int i = 0; i < cn; i++) {
        mbedtls_des3_crypt_ecb(&ctx, buf + i*8, buf + i*8); //Блочное шифрование и запись в этот же буфер
    }
    jbyteArray dout = env->NewByteArray(sz);
    env->SetByteArrayRegion(dout, 0, sz, (jbyte *)buf);
    delete[] buf;
    env->ReleaseByteArrayElements(key, pkey, 0); // Обновляем массивы
    env->ReleaseByteArrayElements(data, pdata, 0);
    return dout;
}


extern "C"
JNIEXPORT jbyteArray JNICALL
Java_ru_igap_gaplikov_1fclient_MainActivity_decrypt(JNIEnv *env, jclass clazz, jbyteArray key,
                                                    jbyteArray data) {
    jsize ksz = env->GetArrayLength(key);
    jsize dsz = env->GetArrayLength(data);
    if ((ksz != 16) || (dsz <= 0) || ((dsz % 8) != 0)) {
        return env->NewByteArray(0);
    }
    mbedtls_des3_context ctx;
    mbedtls_des3_init(&ctx); // инициализация структуры контекста

    jbyte * pkey = env->GetByteArrayElements(key, 0);
    uint8_t * buf = new uint8_t[dsz];

    jbyte * pdata = env->GetByteArrayElements(data, 0);
    std::copy(pdata, pdata + dsz, buf);
    mbedtls_des3_set2key_dec(&ctx, (uint8_t *)pkey); //Установка секретного ключа для дешифрования
    int cn = dsz / 8;
    for (int i = 0; i < cn; i++) {
        mbedtls_des3_crypt_ecb(&ctx, buf + i*8, buf + i*8); //Блочное дешифрование и запись в этот же буфер
    }

    int sz = dsz - 8 + buf[dsz-1]; // Возвращение прежнего размера по значению последнего байта

    jbyteArray dout = env->NewByteArray(sz);
    env->SetByteArrayRegion(dout, 0, sz, (jbyte *)buf);
    delete[] buf;

    env->ReleaseByteArrayElements(key, pkey, 0);
    env->ReleaseByteArrayElements(data, pdata, 0);
    return dout;
}