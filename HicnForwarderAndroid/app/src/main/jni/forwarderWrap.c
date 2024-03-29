/*
 * Copyright (c) 2019 Cisco and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <jni.h>
#include <unistd.h>
#include <stdio.h>
#include <android/log.h>
#include <stdbool.h>
#include <hicn/core/forwarder.h>
#include <parc/security/parc_Security.h>
#include <parc/security/parc_IdentityFile.h>

#include <parc/algol/parc_Memory.h>
#include <parc/algol/parc_SafeMemory.h>
#include <parc/algol/parc_List.h>
#include <parc/algol/parc_ArrayList.h>
#include <hicn/core/dispatcher.h>
#include <parc/algol/parc_FileOutputStream.h>
#include <parc/logging/parc_LogLevel.h>
#include <parc/logging/parc_LogReporterFile.h>
#include <parc/logging/parc_LogReporterTextStdout.h>

#include <parc/assert/parc_Assert.h>

static bool _isRunning = false;
Forwarder *hicnFwd = NULL;


JNIEXPORT jboolean JNICALL
Java_icn_forwarder_com_supportlibrary_Forwarder_isRunning(JNIEnv *env, jobject instance) {
    return _isRunning;
}

static void _setLogLevelToLevel(int logLevelArray[LoggerFacility_END],
                                LoggerFacility facility,
                                const char *levelString) {
    PARCLogLevel level = parcLogLevel_FromString(levelString);

    if (level < PARCLogLevel_All) {
        // we have a good facility and level
        logLevelArray[facility] = level;
    } else {
        printf("Invalid log level string %s\n", levelString);
    }
}

/**
 * string: "facility=level"
 * Set the right thing in the logger
 */
static void _setLogLevel(int logLevelArray[LoggerFacility_END],
                         const char *string) {
    char *tofree = parcMemory_StringDuplicate(string, strlen(string));
    char *p = tofree;

    char *facilityString = strtok(p, "=");
    if (facilityString) {
        char *levelString = strtok(NULL, "=");

        if (strcasecmp(facilityString, "all") == 0) {
            for (LoggerFacility facility = 0; facility < LoggerFacility_END;
                 facility++) {
                _setLogLevelToLevel(logLevelArray, facility, levelString);
            }
        } else {
            LoggerFacility facility;
            for (facility = 0; facility < LoggerFacility_END; facility++) {
                if (strcasecmp(facilityString, logger_FacilityString(facility)) == 0) {
                    break;
                }
            }

            if (facility < LoggerFacility_END) {
                _setLogLevelToLevel(logLevelArray, facility, levelString);
            } else {
                printf("Invalid facility string %s\n", facilityString);
            }
        }
    }

    parcMemory_Deallocate((void **) &tofree);
}

JNIEXPORT void JNICALL
Java_icn_forwarder_com_supportlibrary_Forwarder_start(JNIEnv *env, jobject instance,
                                                      jstring path_, jint capacity) {
    if (!_isRunning) {
        __android_log_print(ANDROID_LOG_DEBUG, "HicnFwdWrap", "starting HicnFwd...");

        Logger *logger = NULL;

        PARCLogReporter *stdoutReporter = parcLogReporterTextStdout_Create();
        logger = logger_Create(stdoutReporter, parcClock_Wallclock());
        parcLogReporter_Release(&stdoutReporter);
        int logLevelArray[LoggerFacility_END];
        /*_setLogLevel(logLevelArray, "all=debug");

        for (int i = 0; i < LoggerFacility_END; i++) {
            if (logLevelArray[i] > -1) {
                logger_SetLogLevel(logger, i, logLevelArray[i]);
            }
        }*/

        hicnFwd = forwarder_Create(logger);
        Configuration *configuration = forwarder_GetConfiguration(hicnFwd);
        if (capacity > 0) {
            configuration_SetObjectStoreSize(configuration, capacity);
        }
        forwarder_SetupLocalListeners(hicnFwd, PORT_NUMBER);
        if (path_) {
            const char *configFileName = (*env)->GetStringUTFChars(env, path_, 0);
            FILE *file = fopen(configFileName, "rb");
            char row[255];
            while (fgets(row, sizeof(row), file) != NULL) {
                __android_log_print(ANDROID_LOG_DEBUG, "HicnFwdWrap", "log file %s", row);
            }

            fclose(file);

            //forwarder_SetupAllListeners(hicnFwd, PORT_NUMBER, NULL);
            forwarder_SetupFromConfigFile(hicnFwd, configFileName);
        }
        Dispatcher *dispatcher = forwarder_GetDispatcher(hicnFwd);
        _isRunning = true;
        dispatcher_Run(dispatcher);
        __android_log_print(ANDROID_LOG_DEBUG, "HicnFwdWrap", "HicnFwd stopped...");
    }
}

JNIEXPORT void JNICALL
Java_icn_forwarder_com_supportlibrary_Forwarder_stop(JNIEnv *env, jobject instance) {
    if (_isRunning) {
        __android_log_print(ANDROID_LOG_DEBUG, "HicnFwdWrap", "stopping HicnFwd...");
        dispatcher_Stop(forwarder_GetDispatcher(hicnFwd));
        sleep(2);
        forwarder_Destroy(&hicnFwd);
        _isRunning = false;
    }
}