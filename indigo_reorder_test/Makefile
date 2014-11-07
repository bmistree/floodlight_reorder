CC=gcc
INDIGO_DIR=../indigo/
LIB_DIR=build/gcc-local/lib/
LIB_FLAGS=-L$(LIB_DIR)

# note: important for cjson.a to come last.  This is because linker
# algorithm works in such a way that when linking static files, will
# only take symbols from library that currently need.  Putting cjson
# at the end ensures that anything that needs it will use it.
LINKED_LIBS=$(LIB_DIR)/SocketManager.a $(LIB_DIR)/AIM.a $(LIB_DIR)/loci.a $(LIB_DIR)/OS.a $(LIB_DIR)/BigHash.a $(LIB_DIR)/Configuration.a $(LIB_DIR)/locitest.a $(LIB_DIR)/BigList.a $(LIB_DIR)/debug_counter.a $(LIB_DIR)/minimatch.a $(LIB_DIR)/timer_wheel.a $(LIB_DIR)/BigRing.a $(LIB_DIR)/indigo.a $(LIB_DIR)/OFConnectionManager.a $(LIB_DIR)/cjson.a


INC_FLAGS=-I$(INDIGO_DIR)/modules/OFStateManager/module/inc/ 
INC_FLAGS += -I$(INDIGO_DIR)/submodules/infra/modules/AIM/module/inc/
INC_FLAGS += -I$(INDIGO_DIR)/modules/indigo/module/inc/
INC_FLAGS += -I$(INDIGO_DIR)/submodules/loxigen-artifacts/loci/inc/
INC_FLAGS += -I$(INDIGO_DIR)/modules/SocketManager/module/inc/
INC_FLAGS += -I$(INDIGO_DIR)/modules/Configuration/module/inc/
INC_FLAGS += -I$(INDIGO_DIR)/submodules/bigcode/modules/cjson/module/inc/
INC_FLAGS += -I$(INDIGO_DIR)/submodules/bigcode/modules/cjson_util/module/inc/


include $(INDIGO_DIR)/init.mk
MODULE := Reorder_utest
TEST_MODULE := Reorder


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

all:
	$(CC) $(LIB_FLAGS) $(INC_FLAGS) main.c $(LINKED_LIBS) -lm -o reorder

