 #############################################################################
 # Copyright (c) 2017 Cisco and/or its affiliates.
 # Licensed under the Apache License, Version 2.0 (the "License");
 # you may not use this file except in compliance with the License.
 # You may obtain a copy of the License at:
 #
 #     http://www.apache.org/licenses/LICENSE-2.0
 #
 # Unless required by applicable law or agreed to in writing, software
 # distributed under the License is distributed on an "AS IS" BASIS,
 # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 # See the License for the specific language governing permissions and
 # limitations under the License.
 ##############################################################################

#!/bin/bash


#!/bin/bash
set -e
ANDROID_ARCH=arm64_v8a
export ANDROID_HOME=${SDK}
export ANDROID_NDK_HOST=${OS}-${ARCH}
export ANDROID_NDK_PLATFORM=android-28
export ANDROID_NDK_ROOT=${NDK}
export ANDROID_SDK_ROOT=${SDK}
export ANDROID_API_VERSION=android-28
export PATH=$PATH:${ANDROID_HOME}/tools:${JAVA_HOME}/bin
export DISTILLARY_INSTALLATION_PATH=${DISTILLERY_ROOT_DIR}/usr_aarch64/
#echo $QT_HOME
#cd ${DISTILLERY_ROOT_DIR}
export DISTILLARY_INSTALLATION_PATH=${DISTILLERY_ROOT_DIR}/usr_aarch64
if [ "$1" = "DEBUG" ]; then
	mkdir -p ${DISTILLERY_BUILD_DIR}/viper_debug
	cd ${DISTILLERY_BUILD_DIR}/viper_debug
	${QT_HOME}/5.12.3/android_${ANDROID_ARCH}/bin/qmake -r -spec android-clang ${DISTILLERY_ROOT_DIR}/src/viper/viper.pro  "TRANSPORT_LIBRARY = HICNET" CONFIG+=debug CONFIG+=qml_debug
	make
	make install INSTALL_ROOT=hicn-viper-${ANDROID_ARCH}
	${QT_HOME}/5.12.3/android_${ANDROID_ARCH}/bin/androiddeployqt --output hicn-viper-${ANDROID_ARCH} --verbose --input android-libviper.so-deployment-settings.json --gradle --android-platform ${ANDROID_NDK_PLATFORM} --stacktrace --debug --target ${ANDROID_NDK_PLATFORM} --debug --sign ${DISTILLERY_ROOT_DIR}/src/viper/android/viper.keystore viper --storepass icn_viper
else
	mkdir -p ${DISTILLERY_BUILD_DIR}/viper
	cd ${DISTILLERY_BUILD_DIR}/viper
	${QT_HOME}/5.12.3/android_${ANDROID_ARCH}/bin/qmake -r -spec android-clang ${DISTILLERY_ROOT_DIR}/src/viper/viper.pro  "TRANSPORT_LIBRARY = HICNET"
	make
	make install INSTALL_ROOT=hicn-viper-${ANDROID_ARCH}
	${QT_HOME}/5.12.3/android_${ANDROID_ARCH}/bin/androiddeployqt --output hicn-viper-${ANDROID_ARCH} --verbose --input android-libviper.so-deployment-settings.json --gradle --android-platform ${ANDROID_NDK_PLATFORM} --stacktrace --release --target ${ANDROID_NDK_PLATFORM} --release --sign ${DISTILLERY_ROOT_DIR}/src/viper/android/viper.keystore viper --storepass icn_viper
fi
cd ..
