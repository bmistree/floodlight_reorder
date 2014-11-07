CC=gcc
INDIGO_DIR=../indigo/

include $(INDIGO_DIR)/init.mk
MODULE := OFStateManager_utest
TEST_MODULE := OFStateManager

LOCI_SOURCE_DIR = $(loci_BASEDIR)/src

# These indicate Linux specific implementations to be used for
# various features
GLOBAL_CFLAGS += -DINDIGO_LINUX_LOGGING
GLOBAL_CFLAGS += -DINDIGO_LINUX_TIME
GLOBAL_CFLAGS += -DINDIGO_FAULT_ON_ASSERT
GLOBAL_CFLAGS += -DINDIGO_MEM_STDLIB
GLOBAL_CFLAGS += -Wall
GLOBAL_CFLAGS += -I${LOCI_SOURCE_DIR}
GLOBAL_CFLAGS += -DAIM_CONFIG_INCLUDE_MODULES_INIT=1
GLOBAL_CFLAGS += -DAIM_CONFIG_INCLUDE_MAIN=1

GLOBAL_CFLAGS += -DOFSTATEMANAGER_CONFIG_INCLUDE_UCLI=0
GLOBAL_CFLAGS += -DSOCKETMANAGER_CONFIG_INCLUDE_UCLI=0
GLOBAL_CFLAGS += -DOFCONNECTIONMANAGER_CONFIG_INCLUDE_UCLI=0

GLOBAL_CFLAGS += -DSOCKETMANAGER_CONFIG_TIMESLICE_MS=1

DEPENDMODULES += AIM BigList SocketManager loci locitest indigo murmur cjson Configuration OFConnectionManager BigHash debug_counter timer_wheel minimatch BigRing OS

GLOBAL_LINK_LIBS += -lm

DEBUG := 1

include $(BUILDER)/standardinit.mk

# At the very least we need the test module
DEPENDMODULES_XHEADER := $(DEPENDMODULES)

# Pull in the utest library for the TEST_MODULE
INCLUDE_$(TEST_MODULE)_UTEST := 1

# Pull in external module dependencies
include $(BUILDER)/dependmodules.mk
include $(BUILDER)/targets.mk
