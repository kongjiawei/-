/*-
 *   BSD LICENSE
 *
 *   Copyright(c) 2010-2016 Intel Corporation. All rights reserved.
 *   All rights reserved.
 *
 *   Redistribution and use in source and binary forms, with or without
 *   modification, are permitted provided that the following conditions
 *   are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in
 *       the documentation and/or other materials provided with the
 *       distribution.
 *     * Neither the name of Intel Corporation nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *   A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *   OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *   LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *   DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *   THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *   OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <inttypes.h>
#include <sys/types.h>
#include <sys/queue.h>
#include <netinet/in.h>
#include <setjmp.h>
#include <stdarg.h>
#include <ctype.h>
#include <errno.h>
#include <getopt.h>
#include <signal.h>
#include <stdbool.h>

#include <rte_common.h>
#include <rte_log.h>
#include <rte_malloc.h>
#include <rte_memory.h>
#include <rte_memcpy.h>
#include <rte_memzone.h>
#include <rte_eal.h>
#include <rte_per_lcore.h>
#include <rte_launch.h>
#include <rte_atomic.h>
#include <rte_cycles.h>
#include <rte_prefetch.h>
#include <rte_lcore.h>
#include <rte_per_lcore.h>
#include <rte_branch_prediction.h>
#include <rte_interrupts.h>
#include <rte_pci.h>
#include <rte_random.h>
#include <rte_debug.h>
#include <rte_ether.h>
#include <rte_ethdev.h>
#include <rte_ring.h>
#include <rte_mempool.h>
#include <rte_mbuf.h>

#include <sys/time.h>

/**
 * tsf: convert this app to int-collector.
 */

/* do not shown dpdk configuration initialization. */
#define CONFIG_NOT_DISPLAY

/* define macro header definition. */
#define ETH_HEADER_LEN              14
#define IPV4_HEADER_LEN             20
#define INT_HEADER_BASE             34
#define INT_HEADER_TYPE_OFF         34
#define INT_HEADER_TTL_OFF          36
#define INT_HEADER_MAPINFO_OFF      37
#define INT_DATA_OFF                38
#define STORE_CNT_THRESHOLD        1000

/* host-byte order <-> network-byte order. */
#define htonll(_x)    ((1==htonl(1)) ? (_x) : \
                           ((uint64_t) htonl(_x) << 32) | htonl(_x >> 32))
#define ntohll(_x)    ((1==ntohl(1)) ? (_x) : \
                           ((uint64_t) ntohl(_x) << 32) | ntohl(_x >> 32))

#define Max(a, b) ((a) >= (b) ? (a) : (b))
#define Min(a, b) ((a) <= (b) ? (a) : (b))
#define Minus(a, b) abs(a-b)

/* test packet processing performance per second. */
#define TEST_SECOND_PERFORMANCE
/* test the INT header. */
#define TEST_INT_HEADER
/* test packet write cost. */
#define FILTER_PKTS

/* define 1s in ms. */
#define ONE_SECOND_IN_MS 1000000.0
/* define 50ms. */
#define FIFTY_MS         50000.0

#define ERROR_THRESH 0.01   /*  in percentage */

#define PRINT_INT_FIELDS            true
#define PRINT_SECOND_PERFORMANCE    true
#define PRINT_SW_CNT                true

/* map_info + switch_id +in_port + out_port + hop_latency + ingress_time + bandwidth + relative_time + cnt. */
static char * INT_FMT = "%x\t%u\t%u\t%u\t%u\t%llx\t%f\t%f\t%u\n";
static char * INT_FIELDS_STR = "map_info\tsw\tin_port\tout_port\tdelay\tin_time\tbandwidth\trelative_time\tcnt%s\n";
static char * INT_PKT_CNT_STR = "sec\t recv_cnt\t sw0\t sw1\t sw2\t sw3\t sw4\t sw5\t sw6%s\n";

/* INT Header: Metadata set. */
typedef struct {
    uint8_t map_info;
//    uint16_t type;
//    uint8_t len;   /* hops */

    uint32_t switch_id;
    uint8_t in_port;
    uint8_t out_port;
    uint16_t hop_latency;
    uint64_t ingress_time;
    float bandwidth;

    uint32_t hash;           /* indicate whether to store into files. */
} item_t;


/* used for storing INT metadata. */
FILE *fp;

/* used to track on pkt_cnt[] */
#define MAX_DEVICE 6
#define MIN_PKT_LEN       60

static void print_pkt(uint32_t pkt_len, uint8_t *pkt){
    uint32_t i = 0;
    for (i = 0; i < pkt_len; ++i) {
        //printf("%02x", i, pkt[i]);

        if ((i + 1) % 8 == 0) {
            printf(" ");   // 2 space
        }

        if ((i + 1) % 16 == 0) {
            printf("\n");
        }
    }
}

static inline unsigned long long rp_get_us(void) {
    struct timeval tv = {0};
    gettimeofday(&tv, NULL);
    return (unsigned long long) (tv.tv_sec * 1000000L + tv.tv_usec);
}

/* used as 'hash' condition for statistics. 'switch_id' or 'ttl' as index. */
uint32_t his_hash[MAX_DEVICE+1] = {0}, hash[MAX_DEVICE+1] = {1, 1, 1, 1, 1, 1};

/* used as 'time_flag' condition for statistics. 'switch_id' or 'ttl' as index. */
uint16_t last_hop_latency[MAX_DEVICE+1] = {1, 1, 1, 1, 1, 1}, time_flag[MAX_DEVICE+1] = {0};

/* used as 'cnt_threshold' condition for statistics. 'switch_id' or 'ttl' as index. */
uint32_t pkt_cnt[MAX_DEVICE+1] = {0};   // sw_id is [1, 2, 3, 4, 5, 6], pkt_cnt[0] used for all. reset when write the record.
uint32_t sw_cnt[MAX_DEVICE+1] = {0};    // count for sw in second, reset per second

/* used for performance test per second. */
uint32_t recv_cnt = 0, sec_cnt = 0, write_cnt = 0;
double start_time[MAX_DEVICE+1] = {0}, end_time[MAX_DEVICE+1] = {0};
double start_time1 = 0, end_time1 = 0;

/* used for relative timestamp. */
double relative_time[MAX_DEVICE+1] = {0}, delta_time = 0;        // write a record with a relative timestamp
double relative_start_time = 0;      // when first pkt comes in, timer runs
bool first_pkt_in = true;                        // when first pkt comes in, turn 'false'

/* used for INT item. */
#define ITEM_SIZE 2048
item_t int_data[ITEM_SIZE] = {0};
float last_bd[MAX_DEVICE+1] = {0};      // the last one bandwidth, last_bd[sw_id]

static volatile bool force_quit;

#define RTE_LOGTYPE_L2FWD RTE_LOGTYPE_USER1

#define NB_MBUF   8192

#define MAX_PKT_BURST 32
#define BURST_TX_DRAIN_US 100 /* TX drain every ~100us */
#define MEMPOOL_CACHE_SIZE 256

/*
 * Configurable number of RX/TX ring descriptors
 */
#define RTE_TEST_RX_DESC_DEFAULT 128
#define RTE_TEST_TX_DESC_DEFAULT 512
static uint16_t nb_rxd = RTE_TEST_RX_DESC_DEFAULT;
static uint16_t nb_txd = RTE_TEST_TX_DESC_DEFAULT;

/* ethernet addresses of ports */
static struct ether_addr l2fwd_ports_eth_addr[RTE_MAX_ETHPORTS];

/* mask of enabled ports */
static uint32_t l2fwd_enabled_port_mask = 0;

/* list of enabled ports */
static uint32_t l2fwd_dst_ports[RTE_MAX_ETHPORTS];

static unsigned int l2fwd_rx_queue_per_lcore = 1;

#define MAX_RX_QUEUE_PER_LCORE 16
#define MAX_TX_QUEUE_PER_PORT 16
struct lcore_queue_conf {
	unsigned n_rx_port;
	unsigned rx_port_list[MAX_RX_QUEUE_PER_LCORE];
} __rte_cache_aligned;
struct lcore_queue_conf lcore_queue_conf[RTE_MAX_LCORE];

static struct rte_eth_dev_tx_buffer *tx_buffer[RTE_MAX_ETHPORTS];

static const struct rte_eth_conf port_conf = {
	.rxmode = {
		.split_hdr_size = 0,
		.header_split   = 0, /**< Header Split disabled */
		.hw_ip_checksum = 0, /**< IP checksum offload disabled */
		.hw_vlan_filter = 0, /**< VLAN filtering disabled */
		.jumbo_frame    = 0, /**< Jumbo Frame Support disabled */
		.hw_strip_crc   = 0, /**< CRC stripped by hardware */
	},
	.txmode = {
		.mq_mode = ETH_MQ_TX_NONE,
	},
};

struct rte_mempool * l2fwd_pktmbuf_pool = NULL;

/* Per-port statistics struct */
struct l2fwd_port_statistics {
	uint64_t tx;
	uint64_t rx;
	uint64_t dropped;
} __rte_cache_aligned;
struct l2fwd_port_statistics port_statistics[RTE_MAX_ETHPORTS];

#define MAX_TIMER_PERIOD 86400 /* 1 day max */
/* A tsc-based timer responsible for triggering statistics printout */
static uint64_t timer_period = 10; /* default period is 10 seconds */

/* Running x second, then automatically quit. used in process_int_pkt() */
static uint32_t timer_interval = 15;   /* default period is 15 seconds */

/* Print out statistics on packets dropped */
static void
print_stats(void)
{
	uint64_t total_packets_dropped, total_packets_tx, total_packets_rx;
	unsigned portid;

	total_packets_dropped = 0;
	total_packets_tx = 0;
	total_packets_rx = 0;

	const char clr[] = { 27, '[', '2', 'J', '\0' };
	const char topLeft[] = { 27, '[', '1', ';', '1', 'H','\0' };

		/* Clear screen and move to top left */
	printf("%s%s", clr, topLeft);

	printf("\nPort statistics ====================================");

	for (portid = 0; portid < 4; portid++) {  // tsf: limited to 4
		/* skip disabled ports */
		if ((l2fwd_enabled_port_mask & (1 << portid)) == 0)
			continue;
		printf("\nStatistics for port %u ------------------------------"
			   "\nPackets sent: %24"PRIu64
			   "\nPackets received: %20"PRIu64
			   "\nPackets dropped: %21"PRIu64,
			   portid,
			   port_statistics[portid].tx,
			   port_statistics[portid].rx,
			   port_statistics[portid].dropped);

		total_packets_dropped += port_statistics[portid].dropped;
		total_packets_tx += port_statistics[portid].tx;
		total_packets_rx += port_statistics[portid].rx;

//		fprintf(fp, "%u\t %lu\t %lt\t %lu\t \n", portid, port_statistics[portid].tx,
//                port_statistics[portid].rx, port_statistics[portid].dropped);

		// clear, then "-T 1" is pkt/s
        port_statistics[portid].tx = 0;
        port_statistics[portid].rx = 0;
        port_statistics[portid].dropped = 0;
	}
	printf("\nAggregate statistics ==============================="
		   "\nTotal packets sent: %18"PRIu64
		   "\nTotal packets received: %14"PRIu64
		   "\nTotal packets dropped: %15"PRIu64,
		   total_packets_tx,
		   total_packets_rx,
		   total_packets_dropped);
	printf("\n====================================================\n");
}

/* equal to rte_pktmbuf_mtod() */
static inline void *
dp_packet_data(const struct rte_mbuf *m)
{
    return m->data_off != UINT16_MAX
           ? (uint8_t *) m->buf_addr + m->data_off : NULL;
}

static uint8_t get_set_bits_of_byte(uint8_t byte){
    uint8_t count = 0;
    while (byte) {
        count += byte & 1;
        byte >>= 1;
    }
    return count;
}


static uint32_t simple_linear_hash(item_t *item) {
    /* hop_latency and ingress_time are volatile, do not hash them. */
    static int prime = 31;
    uint32_t hash = item->switch_id * prime + prime;
    hash += item->in_port * prime;
    hash += item->out_port * prime;
//    hash += ((uint32_t ) item->bandwidth) * prime;
//    hash += item->map_info * prime;

    item->hash = hash;

    return hash;
}

static void signal_handler(int signum);

int i;

/* tsf: parse, filter and collect the INT fields. */
static void process_int_pkt(struct rte_mbuf *m, unsigned portid) {
    uint8_t *pkt = dp_packet_data(m);   // packet header
    uint32_t pkt_len = m->pkt_len;      // packet len

    if (pkt_len < MIN_PKT_LEN) {
        return;
    }

    /* used to indicate where to start to parse. */
    uint8_t pos = INT_HEADER_BASE;

    /*===================== REJECT STAGE =======================*/
#ifdef TEST_INT_HEADER
    uint16_t type = (pkt[pos++] << 8) + pkt[pos++];
    uint8_t ttl = pkt[pos++];
//    printf("type:%d, ttl:%d", type, ttl);
    if (type != 0x0908 || ttl == 0x00) {
        return;
    }
#endif

    /* first_int_pkt_in, init the 'relative_start_time' */
    if (first_pkt_in) {
        relative_start_time = rp_get_us();

        for (i=0; i < (MAX_DEVICE+1); i++) {
            start_time[i] = relative_start_time;
        }
        first_pkt_in = false;
#ifndef PRINT_INT_FIELDS
        printf(INT_FIELDS_STR, "@ok");
#endif
#ifdef PRINT_SW_CNT
        printf(INT_PKT_CNT_STR, "@ok");
#endif
    }

    /* if map_info doesn't contaion INT data. */
    uint8_t map_info = pkt[pos++];
    if (get_set_bits_of_byte(map_info) == 0) {
        return;
    }

    recv_cnt++;                // used for how many packet processed in 1s, clear after per sec.
    int int_idx = recv_cnt % ITEM_SIZE;   //  current index
    int_data[int_idx].map_info = map_info;

    /*===================== PARSE STAGE =======================*/
    uint32_t switch_id = 0x00;
    for (i = 1; i <= ttl; i++) {    // ttl ranges from [1, 6]
        if (map_info & 0x1) {
            switch_id = (pkt[pos++] << 24) + (pkt[pos++] << 16) + (pkt[pos++] << 8) + pkt[pos++];
            sw_cnt[switch_id]++;               // clear per second
        }

        pkt_cnt[switch_id]++;              // clear if printf

        /* calculate the relative time. */
        end_time[switch_id] = rp_get_us();
        relative_time[switch_id] = (end_time[switch_id] - relative_start_time) / ONE_SECOND_IN_MS;  // second
        delta_time = end_time[switch_id] - start_time[switch_id];

        bool should_write = false;
        if (delta_time > FIFTY_MS) { // 50ms, th2
            should_write = true;
            start_time[switch_id] = end_time[switch_id];
        }

        if (map_info & (0x1 << 1)) {
            int_data[int_idx].in_port = pkt[pos++];
        }

        if (map_info & (0x1 << 2)) {
            int_data[int_idx].out_port = pkt[pos++];
        }

        if (map_info & (0x1 << 3)) {
            memcpy(&(int_data[int_idx].ingress_time), &pkt[pos], 8);
            int_data[int_idx].ingress_time = ntohll(int_data[int_idx].ingress_time);
            pos += 8;
        }

        if (map_info & (0x1 << 4)) {
            int_data[int_idx].hop_latency = (pkt[pos++] << 8) + pkt[pos++];
        }

        if (map_info & (0x1 << 5)) {
            memcpy(&(int_data[int_idx].bandwidth), &pkt[pos], 4);
            pos += 4;
        }

#ifdef FILTER_PKTS
        /*===================== FILTER STAGE =======================*/
        /* we don't process no information updating packets. */
        float delta_error = Minus(int_data[int_idx].bandwidth, last_bd[switch_id]) / Min(int_data[int_idx].bandwidth, last_bd[switch_id]);
        last_bd[switch_id] = int_data[int_idx].bandwidth;

        hash[switch_id] = simple_linear_hash(&int_data[int_idx]);

        if ((delta_error > ERROR_THRESH) || (his_hash[switch_id] != hash[switch_id])
            || should_write) {
            start_time[switch_id] = end_time[switch_id];
        } else {
            continue;
        }
#endif

        /* we also store cnt to show how many pkts we last stored as one record. */
#ifndef PRINT_INT_FIELDS
        printf(INT_FMT, int_data[int_idx].map_info, switch_id, int_data[int_idx].in_port,
                int_data[int_idx].out_port, int_data[int_idx].hop_latency, int_data[int_idx].ingress_time,
                int_data[int_idx].bandwidth, relative_time, pkt_cnt[switch_id]);
#endif
        his_hash[switch_id] = hash[switch_id];
        pkt_cnt[switch_id] = 0;
        write_cnt++;
    }

    /* output how many packets we can parse in a second. */
#ifdef TEST_SECOND_PERFORMANCE
    if (recv_cnt == 1) {
        start_time1 = rp_get_us();
        sec_cnt++;
    }

    end_time1 = rp_get_us();

    if (end_time1 - start_time1 >= ONE_SECOND_IN_MS) {

#ifndef PRINT_SECOND_PERFORMANCE
        /* second + recv_pkt/s + write/s */
        printf("%ds\t %d\t %d\n", sec_cnt, recv_cnt, write_cnt);
#endif

#ifdef PRINT_SW_CNT
        printf("%ds\t %d\t %d\t %d\t %d\t %d\t %d\t %d\t %d\n", sec_cnt, recv_cnt, sw_cnt[0], sw_cnt[1], sw_cnt[2],
                sw_cnt[3], sw_cnt[4], sw_cnt[5], sw_cnt[6]);
#endif

        fflush(stdout);
        recv_cnt = 0;
        write_cnt = 0;
        memset(sw_cnt, 0x00, sizeof(sw_cnt));
        start_time1 = end_time1;
    }

    /* auto stop test. 'time_interval'=0 to disable to run. */
    if (!timer_interval || (sec_cnt > timer_interval)) {  // 15s in default, -R [interval] to adjust
        signal_handler(SIGINT);
    }
#endif
}

uint16_t pkt_cnt_v2 = 0;
static void process_int_pkt_v2(struct rte_mbuf *m, unsigned portid) {

    uint8_t *pkt = dp_packet_data(m);   // packet header
    uint32_t pkt_len = m->pkt_len;      // packet len

    if (pkt_len < MIN_PKT_LEN) {
        return;
    }

    /* INT data. */
    uint32_t switch_id = 0x00;
    uint8_t in_port = 0x00;
    uint8_t out_port = 0x00;
    uint16_t hop_latency = 0x00;
    uint64_t ingress_time = 0x00;
    float bandwidth = 0x00;

    uint8_t default_value = 0x00;

    /*===================== REJECT STAGE =======================*/
    /* only process INT packets with TTL > 0. */
    uint8_t pos = INT_HEADER_BASE;

#ifndef TEST
    uint16_t type = (pkt[pos++] << 8) + pkt[pos++];
    uint8_t ttl = pkt[pos++];
    if (type != 0x0908 || ttl == 0x00) {
        return;
    }
#endif

    /*printf("process pkts: %d", pkt_cnt);*/

    /* if map_info doesn't contaion INT data. */
    uint8_t map_info = pkt[pos++];
    if (get_set_bits_of_byte(map_info) == 0) {
        return;
    }

    /*===================== PARSE STAGE =======================*/
    pkt_cnt_v2++;
    recv_cnt++;

    if (map_info & 0x1) {
        switch_id = (pkt[pos++] << 24) + (pkt[pos++] << 16) + (pkt[pos++] << 8) + pkt[pos++];
    }

    if (map_info & (0x1 << 1)) {
        in_port = pkt[pos++];
    }

    if (map_info & (0x1 << 2)) {
        out_port = pkt[pos++];
    }

    if (map_info & (0x1 << 3)) {
        /*ingress_time = (pkt[pos++] << 56) + (pkt[pos++] << 48) + (pkt[pos++] << 40) + (pkt[pos++] << 32) +
                   (pkt[pos++] << 24) + (pkt[pos++] << 16) + (pkt[pos++] << 8) + pkt[pos++];*/
        memcpy(&ingress_time, &pkt[pos],
        sizeof(ingress_time));
        ingress_time = ntohll(ingress_time);
        pos += 8;
    }

    if (map_info & (0x1 << 4)) {
        hop_latency = (pkt[pos++] << 8) + pkt[pos++];
    }

    if (map_info & (0x1 << 5)) {
        memcpy(&bandwidth, &pkt[pos],
        sizeof(bandwidth));
        pos += 4;
    }

/*===================== STORE STAGE =======================*/
item_t item = {
        .switch_id = (switch_id != 0x00 ? switch_id : default_value),
        .in_port = (in_port != 0x00 ? in_port : default_value),
        .out_port = (out_port != 0x00 ? out_port : default_value),
        .hop_latency = (hop_latency != 0x00 ? hop_latency : default_value),
        .ingress_time = (ingress_time != 0x00 ? ingress_time : default_value),
        .bandwidth = (bandwidth != 0x00 ? bandwidth : default_value),
        .map_info = map_info,
};

#ifdef TEST_SECOND_PERFORMANCE
    if (recv_cnt == 1) {
        start_time1 = rp_get_us();
        sec_cnt++;
    }

    end_time1 = rp_get_us();

    if (end_time1 - start_time1 >= 1000000) {
        printf("%d s processed %d packets/s\n", sec_cnt, recv_cnt);
        fflush(stdout);
        recv_cnt = 0;
        start_time1= end_time1;
    }
#endif

/*===================== FILTER STAGE =======================*/
/* we don't process no information updating packets. */
    if ((his_hash[0] != simple_linear_hash(&item)) || (pkt_cnt_v2 > STORE_CNT_THRESHOLD)) {

    } else {
        return;
    }

    printf(INT_FMT, item.map_info, item.switch_id, item.in_port, item.out_port,
         item.hop_latency, item.ingress_time, item.bandwidth, 0, pkt_cnt_v2);

    his_hash[0] = item.hash;
    pkt_cnt_v2 = 0;
}


static void
l2fwd_simple_forward(struct rte_mbuf *m, unsigned portid)
{
	struct ether_hdr *eth;
	void *tmp;
	unsigned dst_port;
	int sent;
	struct rte_eth_dev_tx_buffer *buffer;

	dst_port = l2fwd_dst_ports[portid];
	eth = rte_pktmbuf_mtod(m, struct ether_hdr *);

	/* 02:00:00:00:00:xx */
	tmp = &eth->d_addr.addr_bytes[0];
	*((uint64_t *)tmp) = 0x000000000002 + ((uint64_t)dst_port << 40);

	/* src addr */
	ether_addr_copy(&l2fwd_ports_eth_addr[dst_port], &eth->s_addr);

	buffer = tx_buffer[dst_port];
	sent = rte_eth_tx_buffer(dst_port, 0, buffer, m);
	if (sent)
		port_statistics[dst_port].tx += sent;
}

/* main processing loop */
static void
l2fwd_main_loop(void)
{
	struct rte_mbuf *pkts_burst[MAX_PKT_BURST];
	struct rte_mbuf *m;
	int sent;
	unsigned lcore_id;
	uint64_t prev_tsc, diff_tsc, cur_tsc, timer_tsc;
	unsigned i, j, portid, nb_rx;
	struct lcore_queue_conf *qconf;
	const uint64_t drain_tsc = (rte_get_tsc_hz() + US_PER_S - 1) / US_PER_S *
			BURST_TX_DRAIN_US;
	struct rte_eth_dev_tx_buffer *buffer;

	prev_tsc = 0;
	timer_tsc = 0;

	lcore_id = rte_lcore_id();
	qconf = &lcore_queue_conf[lcore_id];

	if (qconf->n_rx_port == 0) {
		RTE_LOG(INFO, L2FWD, "lcore %u has nothing to do\n", lcore_id);
		return;
	}

	RTE_LOG(INFO, L2FWD, "entering main loop on lcore %u\n", lcore_id);

	for (i = 0; i < qconf->n_rx_port; i++) {

		portid = qconf->rx_port_list[i];
		RTE_LOG(INFO, L2FWD, " -- lcoreid=%u portid=%u\n", lcore_id,
			portid);

	}

	while (!force_quit) {

		cur_tsc = rte_rdtsc();

		/*
		 * TX burst queue drain
		 */
		diff_tsc = cur_tsc - prev_tsc;
		if (unlikely(diff_tsc > drain_tsc)) {

			for (i = 0; i < qconf->n_rx_port; i++) {

				portid = l2fwd_dst_ports[qconf->rx_port_list[i]];
				buffer = tx_buffer[portid];

#ifdef L2_FWD
				sent = rte_eth_tx_buffer_flush(portid, 0, buffer);
				if (sent)
					port_statistics[portid].tx += sent;
#endif

			}

			/* if timer is enabled */
			if (timer_period > 0) {

				/* advance the timer */
				timer_tsc += diff_tsc;

				/* if timer has reached its timeout */
				if (unlikely(timer_tsc >= timer_period)) {

					/* do this only on master core */
					if (lcore_id == rte_get_master_lcore()) {
#ifdef L2_FWD
						print_stats();
#endif
						/* reset the timer */
						timer_tsc = 0;
					}
				}
			}

			prev_tsc = cur_tsc;
		}

		/*
		 * Read packet from RX queues
		 */
		for (i = 0; i < qconf->n_rx_port; i++) {

			portid = qconf->rx_port_list[i];
			nb_rx = rte_eth_rx_burst((uint8_t) portid, 0,
						 pkts_burst, MAX_PKT_BURST);

			port_statistics[portid].rx += nb_rx;

			for (j = 0; j < nb_rx; j++) {
				m = pkts_burst[j];
				rte_prefetch0(rte_pktmbuf_mtod(m, void *));
#ifdef L2_FWD
				l2fwd_simple_forward(m, portid);
#endif
				process_int_pkt(m, portid);         // TNSM response
//                process_int_pkt_v2(m, portid);      // first submission
                rte_pktmbuf_free(m);   /* free the mbuf. */
			}
		}
	}
}

static int
l2fwd_launch_one_lcore(__attribute__((unused)) void *dummy)
{
	l2fwd_main_loop();
	return 0;
}

/* display usage */
static void
l2fwd_usage(const char *prgname)
{
	printf("%s [EAL options] -- -p PORTMASK [-q NQ]\n"
	       "  -p PORTMASK: hexadecimal bitmask of ports to configure\n"
	       "  -q NQ: number of queue (=ports) per lcore (default is 1)\n"
		   "  -T PERIOD: statistics will be refreshed each PERIOD seconds (0 to disable, 10 default, 86400 maximum)\n"
           "  -R INTERVAL: running INTERVAL seconds to quit INT packet processing (0 to disable, 15 default, 86400 maximum)\n",
	       prgname);
}

static int
l2fwd_parse_portmask(const char *portmask)
{
	char *end = NULL;
	unsigned long pm;

	/* parse hexadecimal string */
	pm = strtoul(portmask, &end, 16);
	if ((portmask[0] == '\0') || (end == NULL) || (*end != '\0'))
		return -1;

	if (pm == 0)
		return -1;

	return pm;
}

static unsigned int
l2fwd_parse_nqueue(const char *q_arg)
{
	char *end = NULL;
	unsigned long n;

	/* parse hexadecimal string */
	n = strtoul(q_arg, &end, 10);
	if ((q_arg[0] == '\0') || (end == NULL) || (*end != '\0'))
		return 0;
	if (n == 0)
		return 0;
	if (n >= MAX_RX_QUEUE_PER_LCORE)
		return 0;

	return n;
}

static int
l2fwd_parse_timer_period(const char *q_arg)
{
	char *end = NULL;
	int n;

	/* parse number string */
	n = strtol(q_arg, &end, 10);
	if ((q_arg[0] == '\0') || (end == NULL) || (*end != '\0'))
		return -1;
	if (n >= MAX_TIMER_PERIOD)
		return -1;

	return n;
}


static int
l2fwd_parse_timer_interval(const char *q_arg)
{
    char *end = NULL;
    int n;

    /* parse number string */
    n = strtol(q_arg, &end, 10);
    if ((q_arg[0] == '\0') || (end == NULL) || (*end != '\0'))
        return -1;
    if (n >= MAX_TIMER_PERIOD)
        return -1;

    return n;
}

/* Parse the argument given in the command line of the application */
static int
l2fwd_parse_args(int argc, char **argv)
{
	int opt, ret, timer_secs;
	char **argvopt;
	int option_index;
	char *prgname = argv[0];
	static struct option lgopts[] = {
		{NULL, 0, 0, 0}
	};

	argvopt = argv;

	while ((opt = getopt_long(argc, argvopt, "p:q:T:R:",
				  lgopts, &option_index)) != EOF) {

		switch (opt) {
		/* portmask */
		case 'p':
			l2fwd_enabled_port_mask = l2fwd_parse_portmask(optarg);
			if (l2fwd_enabled_port_mask == 0) {
				printf("invalid portmask\n");
				l2fwd_usage(prgname);
				return -1;
			}
			break;

		/* nqueue */
		case 'q':
			l2fwd_rx_queue_per_lcore = l2fwd_parse_nqueue(optarg);
			if (l2fwd_rx_queue_per_lcore == 0) {
				printf("invalid queue number\n");
				l2fwd_usage(prgname);
				return -1;
			}
			break;

		/* timer period */
		case 'T':
			timer_secs = l2fwd_parse_timer_period(optarg);
			if (timer_secs < 0) {
				printf("invalid timer period\n");
				l2fwd_usage(prgname);
				return -1;
			}
			timer_period = timer_secs;
			break;

        case 'R':
            timer_secs = l2fwd_parse_timer_interval(optarg);
            if (timer_secs < 0) {
                printf("invalid timer period\n");
                l2fwd_usage(prgname);
                return -1;
            }
            timer_interval = timer_secs;
            break;

		/* long options */
		case 0:
			l2fwd_usage(prgname);
			return -1;

		default:
			l2fwd_usage(prgname);
			return -1;
		}
	}

	if (optind >= 0)
		argv[optind-1] = prgname;

	ret = optind-1;
	optind = 0; /* reset getopt lib */
	return ret;
}

/* Check the link status of all ports in up to 9s, and print them finally */
static void
check_all_ports_link_status(uint8_t port_num, uint32_t port_mask)
{
#define CHECK_INTERVAL 100 /* 100ms */
#define MAX_CHECK_TIME 90 /* 9s (90 * 100ms) in total */
	uint8_t portid, count, all_ports_up, print_flag = 0;
	struct rte_eth_link link;

	printf("\nChecking link status");
	fflush(stdout);
	for (count = 0; count <= MAX_CHECK_TIME; count++) {
		if (force_quit)
			return;
		all_ports_up = 1;
		for (portid = 0; portid < port_num; portid++) {
			if (force_quit)
				return;
			if ((port_mask & (1 << portid)) == 0)
				continue;
			memset(&link, 0, sizeof(link));
			rte_eth_link_get_nowait(portid, &link);
			/* print link status if flag set */
			if (print_flag == 1) {
				if (link.link_status)
					printf("Port %d Link Up - speed %u "
						"Mbps - %s\n", (uint8_t)portid,
						(unsigned)link.link_speed,
				(link.link_duplex == ETH_LINK_FULL_DUPLEX) ?
					("full-duplex") : ("half-duplex\n"));
				else
					printf("Port %d Link Down\n",
						(uint8_t)portid);
				continue;
			}
			/* clear all_ports_up flag if any link down */
			if (link.link_status == ETH_LINK_DOWN) {
				all_ports_up = 0;
				break;
			}
		}
		/* after finally printing all link status, get out */
		if (print_flag == 1)
			break;

		if (all_ports_up == 0) {
			printf(".");
			fflush(stdout);
			rte_delay_ms(CHECK_INTERVAL);
		}

		/* set the print_flag if all ports up or timeout */
		if (all_ports_up == 1 || count == (MAX_CHECK_TIME - 1)) {
			print_flag = 1;
			printf("done\n");
		}
	}
}

static void
signal_handler(int signum)
{
	if (signum == SIGINT || signum == SIGTERM) {
		printf("\n\nSignal %d received, preparing to exit...\n",
				signum);
		force_quit = true;

		fflush(stdout);
	}
}

int
main(int argc, char **argv)
{
	struct lcore_queue_conf *qconf;
	struct rte_eth_dev_info dev_info;
	int ret;
	uint8_t nb_ports;
	uint8_t nb_ports_available;
	uint8_t portid, last_port;
	unsigned lcore_id, rx_lcore_id;
	unsigned nb_ports_in_mask = 0;

//	fp = fopen("./int-collector.txt", "rw+");
//	fprintf(fp, INT_STR);
//	fflush(fp);

	/* init EAL */
	ret = rte_eal_init(argc, argv);
	if (ret < 0)
		rte_exit(EXIT_FAILURE, "Invalid EAL arguments\n");
	argc -= ret;
	argv += ret;

	force_quit = false;
	signal(SIGINT, signal_handler);
	signal(SIGTERM, signal_handler);

//	fflush(fp);
//	fclose(fp);

	/* parse application arguments (after the EAL ones) */
	ret = l2fwd_parse_args(argc, argv);
	if (ret < 0)
		rte_exit(EXIT_FAILURE, "Invalid L2FWD arguments\n");

	/* convert to number of cycles */
	timer_period *= rte_get_timer_hz();

	/* create the mbuf pool */
	l2fwd_pktmbuf_pool = rte_pktmbuf_pool_create("mbuf_pool", NB_MBUF,
		MEMPOOL_CACHE_SIZE, 0, RTE_MBUF_DEFAULT_BUF_SIZE,
		rte_socket_id());
	if (l2fwd_pktmbuf_pool == NULL)
		rte_exit(EXIT_FAILURE, "Cannot init mbuf pool\n");

	nb_ports = rte_eth_dev_count();
	if (nb_ports == 0)
		rte_exit(EXIT_FAILURE, "No Ethernet ports - bye\n");

	/* reset l2fwd_dst_ports */
	for (portid = 0; portid < RTE_MAX_ETHPORTS; portid++)
		l2fwd_dst_ports[portid] = 0;
	last_port = 0;

	/*
	 * Each logical core is assigned a dedicated TX queue on each port.
	 */
	for (portid = 0; portid < nb_ports; portid++) {
		/* skip ports that are not enabled */
		if ((l2fwd_enabled_port_mask & (1 << portid)) == 0)
			continue;

		if (nb_ports_in_mask % 2) {
			l2fwd_dst_ports[portid] = last_port;
			l2fwd_dst_ports[last_port] = portid;
		}
		else
			last_port = portid;

		nb_ports_in_mask++;

		rte_eth_dev_info_get(portid, &dev_info);
	}
	if (nb_ports_in_mask % 2) {
		printf("Notice: odd number of ports in portmask.\n");
		l2fwd_dst_ports[last_port] = last_port;
	}

	rx_lcore_id = 0;
	qconf = NULL;

	/* Initialize the port/queue configuration of each logical core */
	for (portid = 0; portid < nb_ports; portid++) {
		/* skip ports that are not enabled */
		if ((l2fwd_enabled_port_mask & (1 << portid)) == 0)
			continue;

		/* get the lcore_id for this port */
		while (rte_lcore_is_enabled(rx_lcore_id) == 0 ||
		       lcore_queue_conf[rx_lcore_id].n_rx_port ==
		       l2fwd_rx_queue_per_lcore) {
			rx_lcore_id++;
			if (rx_lcore_id >= RTE_MAX_LCORE)
				rte_exit(EXIT_FAILURE, "Not enough cores\n");
		}

		if (qconf != &lcore_queue_conf[rx_lcore_id])
			/* Assigned a new logical core in the loop above. */
			qconf = &lcore_queue_conf[rx_lcore_id];

		qconf->rx_port_list[qconf->n_rx_port] = portid;
		qconf->n_rx_port++;
		printf("Lcore %u: RX port %u\n", rx_lcore_id, (unsigned) portid);
	}

	nb_ports_available = nb_ports;

	/* Initialise each port */
	for (portid = 0; portid < nb_ports; portid++) {
		/* skip ports that are not enabled */
		if ((l2fwd_enabled_port_mask & (1 << portid)) == 0) {
			printf("Skipping disabled port %u\n", (unsigned) portid);
			nb_ports_available--;
			continue;
		}
		/* init port */
		printf("Initializing port %u... ", (unsigned) portid);
		fflush(stdout);
		ret = rte_eth_dev_configure(portid, 1, 1, &port_conf);
		if (ret < 0)
			rte_exit(EXIT_FAILURE, "Cannot configure device: err=%d, port=%u\n",
				  ret, (unsigned) portid);

		rte_eth_macaddr_get(portid,&l2fwd_ports_eth_addr[portid]);

		/* init one RX queue */
		fflush(stdout);
		ret = rte_eth_rx_queue_setup(portid, 0, nb_rxd,
					     rte_eth_dev_socket_id(portid),
					     NULL,
					     l2fwd_pktmbuf_pool);
		if (ret < 0)
			rte_exit(EXIT_FAILURE, "rte_eth_rx_queue_setup:err=%d, port=%u\n",
				  ret, (unsigned) portid);

		/* init one TX queue on each port */
		fflush(stdout);
		ret = rte_eth_tx_queue_setup(portid, 0, nb_txd,
				rte_eth_dev_socket_id(portid),
				NULL);
		if (ret < 0)
			rte_exit(EXIT_FAILURE, "rte_eth_tx_queue_setup:err=%d, port=%u\n",
				ret, (unsigned) portid);

		/* Initialize TX buffers */
		tx_buffer[portid] = rte_zmalloc_socket("tx_buffer",
				RTE_ETH_TX_BUFFER_SIZE(MAX_PKT_BURST), 0,
				rte_eth_dev_socket_id(portid));
		if (tx_buffer[portid] == NULL)
			rte_exit(EXIT_FAILURE, "Cannot allocate buffer for tx on port %u\n",
					(unsigned) portid);

		rte_eth_tx_buffer_init(tx_buffer[portid], MAX_PKT_BURST);

		ret = rte_eth_tx_buffer_set_err_callback(tx_buffer[portid],
				rte_eth_tx_buffer_count_callback,
				&port_statistics[portid].dropped);
		if (ret < 0)
				rte_exit(EXIT_FAILURE, "Cannot set error callback for "
						"tx buffer on port %u\n", (unsigned) portid);

		/* Start device */
		ret = rte_eth_dev_start(portid);
		if (ret < 0)
			rte_exit(EXIT_FAILURE, "rte_eth_dev_start:err=%d, port=%u\n",
				  ret, (unsigned) portid);

		printf("done: \n");

		rte_eth_promiscuous_enable(portid);

		printf("Port %u, MAC address: %02X:%02X:%02X:%02X:%02X:%02X\n\n",
				(unsigned) portid,
				l2fwd_ports_eth_addr[portid].addr_bytes[0],
				l2fwd_ports_eth_addr[portid].addr_bytes[1],
				l2fwd_ports_eth_addr[portid].addr_bytes[2],
				l2fwd_ports_eth_addr[portid].addr_bytes[3],
				l2fwd_ports_eth_addr[portid].addr_bytes[4],
				l2fwd_ports_eth_addr[portid].addr_bytes[5]);

		/* initialize port stats */
		memset(&port_statistics, 0, sizeof(port_statistics));
	}

	if (!nb_ports_available) {
		rte_exit(EXIT_FAILURE,
			"All available ports are disabled. Please set portmask.\n");
	}

	check_all_ports_link_status(nb_ports, l2fwd_enabled_port_mask);

	ret = 0;
	/* launch per-lcore init on every lcore */
	rte_eal_mp_remote_launch(l2fwd_launch_one_lcore, NULL, CALL_MASTER);
	RTE_LCORE_FOREACH_SLAVE(lcore_id) {
		if (rte_eal_wait_lcore(lcore_id) < 0) {
			ret = -1;
			break;
		}
	}

	for (portid = 0; portid < nb_ports; portid++) {
		if ((l2fwd_enabled_port_mask & (1 << portid)) == 0)
			continue;
		printf("Closing port %d...", portid);
		rte_eth_dev_stop(portid);
		rte_eth_dev_close(portid);
		printf(" Done\n");
	}
	printf("Bye...\n");

	return ret;
}
