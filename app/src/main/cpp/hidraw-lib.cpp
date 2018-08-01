#include <jni.h>
#include <dirent.h>
#include <fcntl.h>
#include <string>
#include <unistd.h>

//------------------------------------------------------------------------------
#include <android/log.h>

#define TAG "BTRCU_JNI"
#define LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE, TAG,  __VA_ARGS__)
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, TAG,  __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, TAG,  __VA_ARGS__)
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN, TAG,  __VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, TAG,  __VA_ARGS__)

#define GDEBUG
#ifdef  GDEBUG
#define __FILENAME__ (strrchr(__FILE__, '/') ? strrchr(__FILE__, '/') + 1 : __FILE__)
//#define gprintf(msg...)  do { fprintf(stderr,"%s %s() %d: ", __FILE__, __FUNCTION__, __LINE__); fprintf (stderr,msg);} while (0)
#define gprintf(fmt,...)  do { LOGE("%s:%d, %s(): " fmt, __FILENAME__, __LINE__, __FUNCTION__, ##__VA_ARGS__);} while (0)
#else
#define gprintf(fmt,...) do {;} while (0)
#endif
//------------------------------------------------------------------------------

#define HIDRAW_DEV  "/dev/hidraw0"
#define PATH_MAX    60
static char devname[PATH_MAX];
static const unsigned char start_rec_command[] = {0x5a, 0x01};
static const unsigned char stop_rec_command[] = {0x5a, 0x00};

jboolean NisConnected(JNIEnv *env, jobject obj) {
    char *filename;
    DIR *dir;
    struct dirent *de;
    int check_fd = 0;
    jboolean ret = false;

    dir = opendir("/dev");
    if (dir == NULL) {
        gprintf("Can not access to /dev directory.");
        return false;
    }
    strcpy(devname, "/dev/");
    filename = devname + strlen(devname);
    while ((de = readdir(dir))) {
        if (de->d_name[0] == '.' &&
            (de->d_name[1] == '\0' ||
             (de->d_name[1] == '.' && de->d_name[2] == '\0')))
            continue;
        if (memcmp(de->d_name, "hidraw", strlen("hidraw")))
            continue;
        strcpy(filename, de->d_name);
        gprintf("scan_dir: %s\n", devname);
        check_fd = open(devname, O_RDWR);
    }
    if (check_fd != 0) {
        ret = true;
        close(check_fd);
    }
    closedir(dir);
    return ret;
}

int check_devices_state() {
    if (strstr(devname, "hidraw") != NULL) {
        gprintf("There is hidraw devices.");
        return 1;
    } else {
        gprintf("There is NOT hidraw devices.");
        return -1;
    }
}

jint NrecStart(JNIEnv *env, jobject obj) {
    gprintf("NrecStart");
    int fd_hidraw = 0;
    int res = 0;
    if (check_devices_state()>0)
        fd_hidraw = open(devname, O_RDWR);
    else
        fd_hidraw = open(HIDRAW_DEV, O_RDWR);

    gprintf("fd_hidraw : %d", fd_hidraw);
    res = write(fd_hidraw, start_rec_command, 2);
    gprintf("send_message fd:%d, res=%d\n",fd_hidraw, res);
    close(fd_hidraw);
    return 0;
}

jint NrecStop(JNIEnv *env, jobject obj) {
    gprintf("NrecStop");
    int fd_hidraw = 0;
    int res = 0;
    if (check_devices_state()>0)
        fd_hidraw = open(devname, O_RDWR);
    else
        fd_hidraw = open(HIDRAW_DEV, O_RDWR);

    gprintf("fd_hidraw : %d", fd_hidraw);
    res = write(fd_hidraw, stop_rec_command, 2);
    gprintf("send_message fd:%d, res=%d\n",fd_hidraw, res);
    close(fd_hidraw);
    return 0;
}

static JNINativeMethod methods[] = {
        {"isConnected",       "()Z", (void *) NisConnected},
        {"recStart",       "()I", (void *) NrecStart},
        {"recStop",       "()I", (void *) NrecStop},
};

static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = (env)->FindClass(className);
    if (clazz == NULL) {
        gprintf("Native registration unable to find class '%s'", className);
        return JNI_FALSE;
    }
    if ((env)->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        gprintf("RegisterNatives failed for '%s'", className);
        return JNI_FALSE;
    }
    return JNI_TRUE;
}
static const char *classPathName = "com/kakao/i/device/libbtrcu/NativeHIDRaw";

static int registerNatives(JNIEnv *env) {
    if (!registerNativeMethods(env, classPathName, methods, sizeof(methods) / sizeof(methods[0]))) {
        return JNI_FALSE;
    }
    gprintf("registerNativeMethods ok");
    return JNI_TRUE;
}

typedef union {
    JNIEnv *env;
    void *venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    UnionJNIEnvToVoid uenv;
    uenv.venv = NULL;
    jint result = -1;
    JNIEnv *env = NULL;
    gprintf("JNI_OnLoad");
    if ((vm)->GetEnv(&uenv.venv, JNI_VERSION_1_6) != JNI_OK) {
        gprintf("ERROR: GetEnv failed");
        goto bail;
    }
    env = uenv.env;
    if (registerNatives(env) != JNI_TRUE) {
        gprintf("ERROR: registerNatives failed");
        goto bail;
    }
    result = JNI_VERSION_1_6;
    bail:
    return result;
}