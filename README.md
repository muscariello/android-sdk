## Android SDK ##

This is the hICN Distillery software distribution for Android. It is in charge of pulling
together all the necessary modules to build a full hICN software suite for arm64 and x86 Android arch.

## Dependencies ##

Install dependencies

If Ubuntu:

```
sudo apt-get git wget python curl automake libconf libtool openjdk-8-jdk
```

If Mac Os X

```
brew install git wget automake libconf libtool
```


## Quick Start ##

Clone this distro

```
git clone https://github.com/icn-team/android-sdk.git
cd android-sdk
```

Compile everything (dependencies and hICN modules)

```
export ANDROID_ARCH="arm64"
make all
export ANDROID_ARCH="x86"
make all

```

The hICN Distillery software will be installed in android-sdk/usr_aarch64 and android-sdk/usr_i686


To compile Hybrid ICN Network Service for android app

```
make android_hicnforwarder
```

To install the application run

```
adb install -r ./app/build/outputs/apk/release/HicnForwarderAndroid.apk
```

To compile Viper ABR video player for android app 

```
make android_viper
```

To install the application run

```
adb install -r build_aarch64/viper/hicn-viper-arm64_v8a/build/outputs/apk/hicn-viper-arm64_v8a-release-signed.apk
```


## Platforms ##

- Android



## Getting Started ##

To get simple help run `make`. This will give you a list of possible targets to
execute. You will basically want to download all the sources and compile.

Here's a short summary:

- `make help`				- This help message
- `make all`				- Download sdk, ndk and dependencies, configure, compile and install all software in DISTILLERY_INSTALL_DIR
- `make init_depend` 		- Download sdk, ndk and dependencies, compile and install all dependencies in DISTILLERY_INSTALL
- `make update`				- git pull the different modules to the head of master
- `make install-all` 		- Configure, compile and install all software in DISTILLERY_INSTALL_DIR
- `curl-clean`				- Clean curl files and libs
- `openssl-clean`			- Clean opennssl files and libs
- `asio-clean`				- Clean asio files
- `event-clean`				- Clean libevent files and libs
- `ffmpeg-clean`			- Clean ffmpeg files and libs
- `jsoncpp-clean`			- Clean libjsoncpp files and libs
- `xml2-clean`				- Clean libxml2 files and libs
- `libdash-clean`			- Clean libdash files and libs
- `viper-clean`				- Clean viper files
- `dependencies-clean`	 	- Clean all dependencies files and libs
- `sdk-clean`				- Clean sdk files
- `ndk-clean`				- Clean ndk files
- `androidsdk-clean`		- Clean sdk, ndk and cmake files
- `libparc-clean`			- Clean libparc files and libs
- `hicn-clean`				- Clean hicn files and libs
- `all-clean`				- Clean	all files and libs
- `android_hicnforwarder`	- Build HicnForwader apk for android
- `android_hicnforwarder_debug`	- Build HicnForwader apk for android in debug mode
- `android_hicntools`		- Build HicnForwader apk for android
- `android_hicntools_debug`	- Build HicnForwader apk for android in debug mode
- `android_viper`			- Build Viper apk for android apk in debug mode (only arm64)
- `android_viper_debug`		- Build Viper apk for android apk (only arm64)


## Configuration ##

Distillery can be configured in multiple ways.  Please check the config directory (specifically `config/config.mk`) for more information.