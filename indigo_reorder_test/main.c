#include <stdio.h>
#include <OFStateManager/ofstatemanager.h>
#include <OFStateManager/ofstatemanager_config.h>
#include <SocketManager/socketmanager_config.h>
#include <SocketManager/socketmanager.h>
#include <cjson/cJSON.h>
#include <indigo/indigo.h>
#include <indigo/of_connection_manager.h>
#include <indigo/of_state_manager.h>
#include <indigo/port_manager.h>
#include <semaphore.h>

#define RETURN_INT_GOT_REORDERING 1
#define RETURN_INT_NO_REORDERING 0

typedef enum{REORDERING, NO_REORDERING} ReorderingReturnType;

ReorderingReturnType test_reordering(void);
void handle_message(of_object_t *obj);


int main (int argc, char** argv)
{
    ind_core_config_t core;
    ind_soc_config_t soc_cfg = { 0 };
    ind_soc_init(&soc_cfg);
    ind_soc_enable_set(1);
    
    printf("\n\nHello, World!\n\n");
    return 0;
}



ReorderingReturnType test_reordering(void)
{
    // c99 mode: must declare all used variables at top of function.  ug.
    of_flow_add_t* first_add, second_add;
    of_flow_delete_t* del;
    
    of_match_t match;
    uint16_t priority;

    return NO_REORDERING;
}

void handle_message(of_object_t *obj)
{
    indigo_core_receive_controller_message(0, obj);
    of_object_delete(obj);
}


indigo_error_t
indigo_port_modify(of_port_mod_t *port_mod)
{
    //AIM_LOG_VERBOSE("port mod called\n");
    return INDIGO_ERROR_NONE;
}

indigo_error_t
indigo_port_stats_get(of_port_stats_request_t *request,
                      of_port_stats_reply_t **reply_ptr)
{
    *reply_ptr = of_port_stats_reply_new(request->version);
    return INDIGO_ERROR_NONE;
}

indigo_error_t
indigo_port_queue_config_get(of_queue_get_config_request_t *request,
                             of_queue_get_config_reply_t **reply_ptr)
{
    //AIM_LOG_VERBOSE("queue config get called\n");
    *reply_ptr = of_queue_get_config_reply_new(request->version);
    return INDIGO_ERROR_NONE;
}


indigo_error_t
indigo_port_queue_stats_get(of_queue_stats_request_t *request,
                            of_queue_stats_reply_t **reply_ptr)
{

    //AIM_LOG_VERBOSE("queue stats get called\n");
    *reply_ptr = of_queue_stats_reply_new(request->version);
    return INDIGO_ERROR_NONE;
}


indigo_error_t
indigo_port_experimenter(of_experimenter_t *experimenter,
                         indigo_cxn_id_t cxn_id)
{
    //AIM_LOG_VERBOSE("port experimenter called\n");
    return INDIGO_ERROR_NONE;
}

indigo_error_t
indigo_fwd_experimenter(of_experimenter_t *experimenter,
                        indigo_cxn_id_t cxn_id)
{
    //AIM_LOG_VERBOSE("port experimenter called\n");
    return INDIGO_ERROR_NONE;
}

indigo_error_t
indigo_port_interface_list(indigo_port_info_t **list)
{
    *list = NULL;
    return INDIGO_ERROR_NONE;
}

void
indigo_port_interface_list_destroy(indigo_port_info_t *list)
{
}

indigo_error_t indigo_port_desc_stats_get(
    of_port_desc_stats_reply_t *port_desc_stats_reply)
{
    //AIM_LOG_VERBOSE("port desc stats get called");
    return INDIGO_ERROR_NONE;
}

void
indigo_fwd_pipeline_get(of_desc_str_t pipeline)
{
    //AIM_LOG_VERBOSE("fwd switch pipeline get");
    strcpy(pipeline, "some_pipeline");
}

indigo_error_t
indigo_fwd_pipeline_set(of_desc_str_t pipeline)
{
    //AIM_LOG_VERBOSE("fwd switch pipeline set: %s", pipeline);
    return INDIGO_ERROR_NONE;
}

void
indigo_fwd_pipeline_stats_get(of_desc_str_t **pipeline, int *num_pipelines)
{
    //AIM_LOG_VERBOSE("fwd switch pipeline stats get");
    *num_pipelines = 0;
}


indigo_error_t
indigo_port_features_get(of_features_reply_t *features)
{
    //AIM_LOG_VERBOSE("port features get called\n");
    return INDIGO_ERROR_NONE;
}

indigo_error_t
indigo_fwd_forwarding_features_get(of_features_reply_t *features)
{
    //AIM_LOG_VERBOSE("forwarding features get called\n");
    return INDIGO_ERROR_NONE;
}

indigo_error_t
indigo_fwd_packet_out(of_packet_out_t *of_packet_out)
{
//    AIM_LOG_VERBOSE("packet out called\n");
    return INDIGO_ERROR_NONE;
}
