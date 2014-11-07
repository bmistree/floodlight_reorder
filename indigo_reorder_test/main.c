#include <stdio.h>
#include <indigo/time.h>
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
#include "ofstatemanager_decs.h"

typedef enum{REORDERING, NO_REORDERING, REORDERING_ERROR} ReorderingReturnType;

ReorderingReturnType test_reordering(void);
void handle_message(of_object_t *obj);


static indigo_error_t
op_entry_create(void *table_priv, indigo_cxn_id_t cxn_id,
                of_flow_add_t *obj, indigo_cookie_t flow_id, void **entry_priv)
{
    //AIM_LOG_VERBOSE("flow create called");
    *entry_priv = NULL;
    return INDIGO_ERROR_NONE;
}

static indigo_error_t
op_entry_modify(void *table_priv, indigo_cxn_id_t cxn_id,
                void *entry_priv, of_flow_modify_t *obj)
{
    //AIM_LOG_VERBOSE("flow modify called");
    return INDIGO_ERROR_NONE;
}

static indigo_error_t
op_entry_delete(void *table_priv, indigo_cxn_id_t cxn_id,
                void *entry_priv, indigo_fi_flow_stats_t *flow_stats)
{
    //AIM_LOG_VERBOSE("flow delete called");
    memset(flow_stats, 0, sizeof(*flow_stats));
    return INDIGO_ERROR_NONE;
}

static indigo_error_t
op_entry_stats_get(void *table_priv, indigo_cxn_id_t cxn_id,
                   void *entry_priv, indigo_fi_flow_stats_t *flow_stats)
{
    //AIM_LOG_VERBOSE("flow stats get called");
    memset(flow_stats, 0, sizeof(*flow_stats));
    return INDIGO_ERROR_NONE;
}

static indigo_error_t
op_entry_hit_status_get(void *table_priv, indigo_cxn_id_t cxn_id,
                        void *entry_priv, bool *hit_status)
{
    //AIM_LOG_VERBOSE("flow hit status get called");
    *hit_status = false;
    return INDIGO_ERROR_NONE;
}


static indigo_core_table_ops_t test_ops = {
    op_entry_create,
    op_entry_modify,
    op_entry_delete,
    op_entry_stats_get,
    op_entry_hit_status_get,
};


int main (int argc, char** argv)
{
    ind_core_config_t core;
    ind_soc_config_t soc_cfg = { 0 };
    ind_soc_init(&soc_cfg);
    ind_soc_enable_set(1);

    /* Init Core */
    MEMSET(&core, 0, sizeof(core));
    core.stats_check_ms = 1000;

    if (ind_core_init(&core) != INDIGO_ERROR_NONE)
    {
        printf("Error in initing core");
        assert(false);
    }
    if (ind_core_enable_set(1) != INDIGO_ERROR_NONE)
    {
        printf("Error enabling core");
        assert(false);
    }

    indigo_core_table_register(0, "test", &test_ops, NULL);
    
    
    
    printf("\n\nHello, World!\n\n");
    return 0;
}



void do_barrier()
{
    // FIXME: not a real barrier.  Just wait a while to force buffered
    // commands to run.
    int idx;
    
    for (idx = 0; idx < 1000; ++idx)
        ind_soc_select_and_run(0);
}

ReorderingReturnType test_reordering(void)
{
    // c99 mode: must declare all used variables at top of function.  ug.
    of_flow_add_t* first_add, second_add;
    of_flow_delete_t* del;
    
    of_match_t match;
    uint16_t priority;


    // issue barrier so that all commands finish
    printf("\nExecuting barrier\n");
    do_barrier();

    if (ind_core_ft->current_count == 0)
        return REORDERING;
    else if (ind_core_ft->current_count == 1)
        return NO_REORDERING;

    printf(
        "\nUnexpected number of flow table entries: %i\n",
        ind_core_ft->current_count);
    
    assert(false);
    return REORDERING_ERROR;
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
