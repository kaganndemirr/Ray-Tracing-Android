cmake_minimum_required(VERSION 3.6.0)

# build vulkan app
set(SRC_DIR ../src/main/cpp/)
set(BASE_DIR ./base)
set(THIRDPARTY_DIR ./thirdparty)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++17 -Wall -Werror -DVK_USE_PLATFORM_ANDROID_KHR")

file(GLOB EXAMPLE_SRC "${SRC_DIR}/*.cpp")

add_library(native-lib SHARED ${EXAMPLE_SRC})

add_library(native-app-glue STATIC ${ANDROID_NDK}/sources/android/native_app_glue/android_native_app_glue.c)

set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -u ANativeActivity_onCreate")

include_directories(${BASE_DIR})
include_directories(${THIRDPARTY_DIR})
include_directories(${THIRDPARTY_DIR}/glm)
include_directories(${THIRDPARTY_DIR}/gli)
include_directories(${THIRDPARTY_DIR}/imgui)
include_directories(${THIRDPARTY_DIR}/assimp)
include_directories(${ANDROID_NDK}/sources/android/native_app_glue)

target_link_libraries(
        native-lib
        native-app-glue
        libbase
        android
        log
        z
)