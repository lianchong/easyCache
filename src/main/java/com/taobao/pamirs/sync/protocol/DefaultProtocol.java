////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2010,  Taobao. All rights reserved.
//
////////////////////////////////////////////////////////////////////////////

package com.taobao.pamirs.sync.protocol;

/**
 * 
 */
public class DefaultProtocol
{
    public static final String TCP = "   TCP(   " + "                     bind_port=9981;   "
                                     + "                     start_port=9981;  " + "                     loopback=true;    "
                                     + "                     recv_buf_size=20000000;   "
                                     + "                     send_buf_size=640000; "
                                     + "                     discard_incompatible_packets=true;    " + "                       "
                                     + "                     max_bundle_size=64000;    "
                                     + "                     max_bundle_timeout=30;    "
                                     + "                     use_incoming_packet_handler=true; "
                                     + "                     enable_bundling=true; "
                                     + "                     use_send_queues=false;    "
                                     + "                     sock_conn_timeout=300;    "
                                     + "                     skip_suspected_members=true;  "
                                     + "                     use_concurrent_stack=true;    " + "                       "
                                     + "                     thread_pool.enabled=true; "
                                     + "                     thread_pool.min_threads=1;    "
                                     + "                     thread_pool.max_threads=25;   "
                                     + "                     thread_pool.keep_alive_time=5000; "
                                     + "                     thread_pool.queue_enabled=false;  "
                                     + "                     thread_pool.queue_max_size=100;   "
                                     + "                     thread_pool.rejection_policy=run; " + "                   "
                                     + "                     oob_thread_pool.enabled=true; "
                                     + "                     oob_thread_pool.min_threads=1;    "
                                     + "                     oob_thread_pool.max_threads=8;    "
                                     + "                     oob_thread_pool.keep_alive_time=5000; "
                                     + "                     oob_thread_pool.queue_enabled=false;  "
                                     + "                     oob_thread_pool.queue_max_size=100;   "
                                     + "                     oob_thread_pool.rejection_policy=run  " + "                  ):   "
                                     + "                   " + "               TCPPING(    "
                                     + "                     initial_hosts=" + "CLUSTER_MEMBERS" + ";"
                                     + "                     port_range=1; " + "                     timeout=3000;     "
                                     + "                     num_initial_members=3;    " + "                     ):    "
                                     + "              VERIFY_SUSPECT(  " + "                   timeout=5500;   "
                                     + "                       ):  " + "               MERGE2(     "
                                     + "                       max_interval=100000;    "
                                     + "                       min_interval=20000      " + "                       ):      "
                                     + "               pbcast.NAKACK(  " + "                       use_mcast_xmit=false;   "
                                     + "                       gc_lag=50;  " + "                       retransmit_timeout=3000 "
                                     + "                       ):  " + "              FD_SOCK: " + "               FD(     "
                                     + "                   timeout=10000;  " + "                   max_tries=5;    "
                                     + "                   shun=true   " + "               ):  "
                                     + "               pbcast.STABLE(  " + "                   stability_delay=1000;   "
                                     + "                   desired_avg_gossip=50000    " + "               ):  "
                                     + "               VIEW_SYNC(avg_send_interval=60000): " + "               FC( "
                                     + "                   max_credits=2000000;    " + "                   min_threshold=0.10  "
                                     + "               ):  " + "               FRAG2(frag_size=60000): "
                                     + "               pbcast.STREAMING_STATE_TRANSFER:    " + "               pbcast.GMS( "
                                     + "                   join_timeout=5000;  " + "                   shun=true;  "
                                     + "                   print_local_addr=true;  " + "                   view_bundling=true  "
                                     + ")";
}
