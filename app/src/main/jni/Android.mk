LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
OPENCV_INSTALL_MODULES:=on
OPENCV_CAMERA_MODULES:=off
include ..\..\..\..\native\jni\OpenCV.mk
LOCAL_MODULE    := nonfree
LOCAL_LDLIBS    += -llog
LOCAL_SRC_FILES := nonfree_init.cpp \
 precomp.hpp \
 sift.cpp \
 surf.cpp
include $(BUILD_SHARED_LIBRARY)