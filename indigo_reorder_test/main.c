#include <stdio.h>
#include <OFStateManager/ofstatemanager.h>
#include <OFStateManager/ofstatemanager_config.h>
#include <SocketManager/socketmanager_config.h>
#include <SocketManager/socketmanager.h>
#include <cjson/cJSON.h>

int main (int argc, char** argv)
{
    ind_core_config_t core;
    ind_soc_config_t soc_cfg = { 0 };
    ind_soc_init(&soc_cfg);
    
    /* ind_soc_enable_set(1); */
    
    printf("\n\nHello, World!\n\n");
    return 0;
}
