/*
 * Copyright 2018-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.test.action;

import com.google.common.collect.ImmutableList;
import org.apache.felix.scr.annotations.*;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.core.GroupId;
import org.onosproject.floodlightpof.protocol.OFMatch20;
import org.onosproject.floodlightpof.protocol.action.OFAction;
import org.onosproject.floodlightpof.protocol.table.OFFlowTable;
import org.onosproject.floodlightpof.protocol.table.OFTableType;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DeviceAdminService;
import org.onosproject.net.flow.*;
import org.onosproject.net.flow.criteria.Criteria;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.flow.instructions.DefaultPofActions;
import org.onosproject.net.flow.instructions.DefaultPofInstructions;
import org.onosproject.net.group.*;
import org.onosproject.net.table.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tsf
 * @created 2020-04-10
 */

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class AppComponent {

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowTableStore flowTableStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceAdminService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowTableStore tableStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowTableService flowTableService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected GroupService groupService;

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationId appId;

    /**
     * port ids.
     */
    int port1 = 1;
    int port2 = 2;
    int port3 = 3;
    int controller_port = (int) PortNumber.CONTROLLER.toLong();

    /**
     * network pof device.
     */
    private DeviceId sw1 = DeviceId.deviceId("pof:0000000000000001");
    private DeviceId sw2 = DeviceId.deviceId("pof:0000000000000002");
    private DeviceId sw3 = DeviceId.deviceId("pof:0000000000000003");
    private DeviceId sw4 = DeviceId.deviceId("pof:0000000000000004");
    private DeviceId sw5 = DeviceId.deviceId("pof:0000000000000005");
    private DeviceId sw6 = DeviceId.deviceId("pof:0000000000000006");
    private DeviceId sw7 = DeviceId.deviceId("pof:0000000000000007");

    /**
     * global tableId.
     */
    private byte sw1_tbl0, sw1_tbl1;
    private byte sw2_tbl0, sw2_tbl1;
    private byte sw3_tbl0, sw3_tbl1;
    private byte sw4_tbl0, sw4_tbl1;
    private byte sw5_tbl0, sw5_tbl1;
    private byte sw6_tbl0, sw6_tbl1;
    private byte sw7_tbl0, sw7_tbl1;

    /**
     *  match field values.
     */
    private String srcIp = Protocol.IPV4_SIP_VAL;
    private String int_type = Protocol.INT_TYPE_VAL;


    @Activate
    protected void activate() {
        appId = coreService.registerApplication("org.onosproject.int.action");

//        pofTestStart_INT_Insertion_for_single_node();

//        pofTestStart_INT_Insertion_for_path();

        pofTestStart_INT_Insertion_for_seven_nodes();
    }


    @Deactivate
    protected void deactivate() {

//        pofTestStop_INT_Insertion_for_single_node();

//        pofTestStop_INT_Insertion_for_path();

        pofTestStop_INT_Insertion_for_seven_nodes();
    }

    public void pofTestStart_INT_Insertion_for_seven_nodes() {
        log.info("org.onosproject.pof.test.action Started");

//        sw1_tbl0 = send_pof_flow_table_match_SIP_at_SRC(sw1, "AddIntHeader");
//        sw2_tbl0 = send_pof_flow_table_match_INT_TYPE_at_INTER(sw2, "AddIntMetadata");
//        sw3_tbl0 = send_pof_flow_table_match_INT_TYPE_at_INTER(sw3, "AddIntMetadata");
//        sw4_tbl0 = send_pof_flow_table_match_INT_TYPE_at_INTER(sw4, "AddIntMetadata");
//        sw5_tbl0 = send_pof_flow_table_match_INT_TYPE_at_INTER(sw5, "AddIntMetadata");
//        sw6_tbl0 = send_pof_flow_table_match_INT_TYPE_at_INTER(sw6, "MirrorIntMetadata");
//        sw7_tbl0 = send_pof_flow_table_match_INT_TYPE_at_INTER(sw7, "MirrorIntMetadata");
          sw7_tbl0 = send_pof_flow_table_match_SIP_at_SRC(sw7, "AddIntHeader");
//          sw7_tbl0 = send_pof_flow_table_match_INT_TYPE_at_INTER(sw7, "AddIntMetadata");

        /**
         * wait 1s
         */
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String mapInfo = "0fff";
        int sampling_rate_N = 2;

        /**
         * SRC(sw1): send flow table match src_ip{208, 32}
         */
        /* rule1: send add_int_field rule to insert INT header in 1/N, the key->len refers to 'N'.*/
//        install_pof_add_int_field_rule_match_srcIp(sw1, sw1_tbl0, srcIp, port2, 12, mapInfo, sampling_rate_N);
//        install_pof_FWD_MOD_FIELD_rule_match_srcIP(sw1, sw1_tbl0, srcIp, port1, 12, mapInfo, sampling_rate_N);
        /* rule2: default rule, mask is 0x00000000 */
//        install_pof_output_flow_rule_match_default_ip_at_SRC(sw1, sw1_tbl0, srcIp, port1, 1);


        /** INTER(sw2): send flow table match int_type{272, 16} */
        /* rule1: add_int_action. if mapInfo = '0xffff', switch reads mapInfo value from packet instead of controller. */
//        install_pof_add_int_field_rule_match_type(sw2, sw2_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
 //      install_pof_FWD_MOD_FIELD_rule_match_type(sw2, sw2_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
        /* rule2: default rule, mask is 0x0000 */
//        install_pof_output_flow_rule_match_default_type_at_INTER_or_SINK(sw2, sw2_tbl0, int_type, port2, 1);


        /** INTER(sw3): send flow table match int_type{272, 16} */
        /* rule1: add_int_action. if mapInfo = '0xffff', switch reads mapInfo value from packet instead of controller. */
//        install_pof_add_int_field_rule_match_type(sw3, sw3_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
 //       install_pof_FWD_MOD_FIELD_rule_match_type(sw3, sw3_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
        /* rule2: default rule, mask is 0x0000 */
//        install_pof_output_flow_rule_match_default_type_at_INTER_or_SINK(sw3, sw2_tbl0, int_type, port2, 1);


        /** INTER(sw4): send flow table match int_type{272, 16} */
        /* rule1: add_int_action. if mapInfo = '0xffff', switch reads mapInfo value from packet instead of controller.  */
//        install_pof_add_int_field_rule_match_type(sw4, sw4_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
//        install_pof_FWD_MOD_FIELD_rule_match_type(sw4, sw4_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
        /* rule2: default rule, mask is 0x0000 */
//        install_pof_output_flow_rule_match_default_type_at_INTER_or_SINK(sw4, sw4_tbl0, int_type, port2, 1);


        /** INTER(sw5): send flow table match int_type{272, 16} */
        /* rule1: add_int_action. if mapInfo = '0xffff', switch reads mapInfo value from packet instead of controller.  */
//        install_pof_add_int_field_rule_match_type(sw5, sw5_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
//        install_pof_FWD_MOD_FIELD_rule_match_type(sw5, sw5_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
        /* rule2: default rule, mask is 0x0000 */
//        install_pof_output_flow_rule_match_default_type_at_INTER_or_SINK(sw5, sw5_tbl0, int_type, port2, 1);

        /** INTER(sw6): send flow table match int_type{272, 16} */
        /* rule1: add_int_action. if mapInfo = '0xffff', switch reads mapInfo value from packet instead of controller.  */
//        install_pof_add_int_field_rule_match_type(sw6, sw6_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
//        install_pof_FWD_MOD_FIELD_rule_match_type(sw6, sw6_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
        /* rule2: default rule, mask is 0x0000 */
//        install_pof_output_flow_rule_match_default_type_at_INTER_or_SINK(sw6, sw6_tbl0, int_type, port2, 1);

        /** INTER(sw7) - without mirror: send flow table match int_type{272, 16} */
        /* rule1: add_int_action. if mapInfo = '0xffff', switch reads mapInfo value from packet instead of controller.  */
//        install_pof_add_int_field_rule_match_type(sw7, sw7_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
//       install_pof_FWD_MOD_FIELD_rule_match_type(sw7, sw7_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
        /* rule2: default rule, mask is 0x0000 */
//        install_pof_output_flow_rule_match_default_type_at_INTER_or_SINK(sw7, sw7_tbl0, int_type, port2, 1);

        /** SINK(sw7) - with mirror: send flow table match int_type{272, 16} */
        /* rule1: mirror INT packets to collector and usr */
//        install_pof_all_group_rule_match_type(sw7, sw7_tbl0, int_type, Protocol.all_key, Protocol.all_groupId, 12, port2, port3, Protocol.DATA_PLANE_MAPINFO_VAL);
//        install_pof_group_rule_match_type(sw7, sw7_tbl0, int_type, Protocol.all_groupId, 12);
        /* rule2: default rule, mask is 0x0000*/
//        install_pof_output_flow_rule_match_default_type_at_INTER_or_SINK(sw7, sw7_tbl0, int_type, port2, 1);  // usr_port
        install_pof_FWD_MOD_FIELD_rule_match_srcIP(sw7, sw7_tbl0, srcIp, port2, 12, mapInfo, sampling_rate_N);
    }

    public void pofTestStop_INT_Insertion_for_seven_nodes() {
        remove_pof_group_tables(sw7, Protocol.all_key);

        /* remove flow tables */
//        remove_pof_flow_table(sw1, sw1_tbl0);
//        remove_pof_flow_table(sw2, sw2_tbl0);
//        remove_pof_flow_table(sw3, sw3_tbl0);
//
//        remove_pof_flow_table(sw4, sw4_tbl0);
//        remove_pof_flow_table(sw5, sw5_tbl0);
//        remove_pof_flow_table(sw6, sw6_tbl0);
        remove_pof_flow_table(sw7, sw7_tbl0);
        log.info("org.onosproject.test.action Stopped: all flow/group tables are removed!");
    }

    public void pofTestStart_INT_Insertion_for_single_node() {
        log.info("org.onosproject.pof.test.action Started");

        sw1_tbl0 = send_pof_flow_table_match_SIP_at_SRC(sw7, "AddIntHeader");

        /**
         * wait 1s
         */
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String mapInfo = "0fff";
        int sampling_rate_N = 2;

        /**
         * SRC(sw1): send flow table match src_ip{208, 32}
         */
        /* rule1: send add_int_field rule to insert INT header in 1/N, the key->len refers to 'N'.*/
        install_pof_add_int_field_rule_match_srcIp(sw7, sw1_tbl0, srcIp, port1, 12, mapInfo, sampling_rate_N);
        /* rule2: default rule, mask is 0x00000000 */
//        install_pof_output_flow_rule_match_default_ip_at_SRC(sw1, sw1_tbl0, srcIp, port2, 1);
    }

    public void pofTestStop_INT_Insertion_for_single_node() {
        /* remove flow tables */
        remove_pof_flow_table(sw7, sw7_tbl0);

        log.info("org.onosproject.test.action Stopped: all flow/group tables are removed!");
    }


    public void pofTestStart_INT_Insertion_for_path() {
        log.info("org.onosproject.pof.test.action Started");

        sw1_tbl0 = send_pof_flow_table_match_SIP_at_SRC(sw1, "AddIntHeader");
        sw2_tbl0 = send_pof_flow_table_match_INT_TYPE_at_INTER(sw2, "AddIntMetadata");
        sw3_tbl0 = send_pof_flow_table_match_INT_TYPE_at_INTER(sw3, "AddIntMetadata");
        sw4_tbl0 = send_pof_flow_table_match_INT_TYPE_at_INTER(sw4, "AddIntMetadata");
        sw5_tbl0 = send_pof_flow_table_match_INT_TYPE_at_INTER(sw5, "AddIntMetadata");
        sw6_tbl0 = send_pof_flow_table_match_INT_TYPE_at_INTER(sw6, "MirrorIntMetadata");

        /**
         * wait 1s
         */
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String mapInfo = "0020";
        int sampling_rate_N = 50;

        /**
         * SRC(sw1): send flow table match src_ip{208, 32}
         */
        /* rule1: send add_int_field rule to insert INT header in 1/N, the key->len refers to 'N'.*/
        install_pof_add_int_field_rule_match_srcIp(sw1, sw1_tbl0, srcIp, port3, 12, mapInfo, sampling_rate_N);
        /* rule2: default rule, mask is 0x00000000 */
        install_pof_output_flow_rule_match_default_ip_at_SRC(sw1, sw1_tbl0, srcIp, port3, 1);

        /** INTER(sw2): send flow table match int_type{272, 16} */
        /* rule1: add_int_action. if revalidate path, with add_func_field action */
        install_pof_add_int_field_rule_match_type(sw2, sw2_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
        /* rule2: default rule, mask is 0x0000 */
        install_pof_output_flow_rule_match_default_type_at_INTER_or_SINK(sw2, sw2_tbl0, int_type, port2, 1);


        /** INTER(sw3): send flow table match int_type{272, 16} */
        /* rule1: add_int_action. if revalidate path, with add_func_field action */
        install_pof_add_int_field_rule_match_type(sw3, sw3_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
        /* rule2: default rule, mask is 0x0000 */
        install_pof_output_flow_rule_match_default_type_at_INTER_or_SINK(sw3, sw2_tbl0, int_type, port2, 1);


        /** INTER(sw4): send flow table match int_type{272, 16} */
        /* rule1: add_int_action. if revalidate path, with add_func_field action */
        install_pof_add_int_field_rule_match_type(sw4, sw4_tbl0, int_type, port2, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
        /* rule2: default rule, mask is 0x0000 */
        install_pof_output_flow_rule_match_default_type_at_INTER_or_SINK(sw4, sw4_tbl0, int_type, port2, 1);


        /** INTER(sw5): send flow table match int_type{272, 16} */
        /* rule1: add_int_action. if revalidate path, with add_func_field action */
        install_pof_add_int_field_rule_match_type(sw5, sw5_tbl0, int_type, port3, 12, Protocol.DATA_PLANE_MAPINFO_VAL);
        /* rule2: default rule, mask is 0x0000 */
        install_pof_output_flow_rule_match_default_type_at_INTER_or_SINK(sw5, sw5_tbl0, int_type, port3, 1);

        /** SINK(sw6): send flow table match int_type{272, 16} */
        /* rule1: mirror INT packets to collector and usr */
        install_pof_all_group_rule_match_type(sw6, sw6_tbl0, int_type, Protocol.all_key, Protocol.all_groupId, 12, port2, port3, Protocol.DATA_PLANE_MAPINFO_VAL);
        install_pof_group_rule_match_type(sw6, sw2_tbl0, int_type, Protocol.all_groupId, 12);
        /* rule2: default rule, mask is 0x0000*/
        install_pof_output_flow_rule_match_default_type_at_INTER_or_SINK(sw6, sw6_tbl0, int_type, port2, 1);  // usr_port

    }


    public void pofTestStop_INT_Insertion_for_path() {
        remove_pof_group_tables(sw6, Protocol.all_key);

        /* remove flow tables */
        remove_pof_flow_table(sw1, sw1_tbl0);
        remove_pof_flow_table(sw2, sw2_tbl0);
        remove_pof_flow_table(sw3, sw3_tbl0);

        remove_pof_flow_table(sw4, sw4_tbl0);
        remove_pof_flow_table(sw5, sw5_tbl0);
        remove_pof_flow_table(sw6, sw6_tbl0);
        log.info("org.onosproject.test.action Stopped: all flow/group tables are removed!");
    }


    public byte send_pof_flow_table_match_SIP_at_SRC(DeviceId deviceId, String table_name) {
        byte globeTableId = (byte) tableStore.getNewGlobalFlowTableId(deviceId, OFTableType.OF_MM_TABLE);
        byte tableId = tableStore.parseToSmallTableId(deviceId, globeTableId);

        OFMatch20 srcIP = new OFMatch20();
        srcIP.setFieldId(Protocol.SIP_ID);
        srcIP.setFieldName("srcIP");
        srcIP.setOffset(Protocol.IPV4_SIP_OFF);
        srcIP.setLength(Protocol.IPV4_SIP_LEN);

        ArrayList<OFMatch20> match20List = new ArrayList<>();
        match20List.add(srcIP);

        OFFlowTable ofFlowTable = new OFFlowTable();
        ofFlowTable.setTableId(tableId);
        ofFlowTable.setTableName(table_name);
        ofFlowTable.setMatchFieldList(match20List);
        ofFlowTable.setMatchFieldNum((byte) 1);
        ofFlowTable.setTableSize(32);
        ofFlowTable.setTableType(OFTableType.OF_MM_TABLE);
        ofFlowTable.setCommand(null);
        ofFlowTable.setKeyLength((short) 32);

        FlowTable.Builder flowTable = DefaultFlowTable.builder()
                .withFlowTable(ofFlowTable)
                .forTable(tableId)
                .forDevice(deviceId)
                .fromApp(appId);

        flowTableService.applyFlowTables(flowTable.build());

        log.info("table<{}> applied to device<{}> successfully.", tableId, deviceId.toString());

        return tableId;
    }


    public byte send_pof_flow_table_match_INT_TYPE_at_INTER(DeviceId deviceId, String table_name) {
        byte globeTableId = (byte) tableStore.getNewGlobalFlowTableId(deviceId, OFTableType.OF_MM_TABLE);
        byte tableId = tableStore.parseToSmallTableId(deviceId, globeTableId);

        OFMatch20 int_type = new OFMatch20();
        int_type.setFieldId(Protocol.INT_TYPE_ID);
        int_type.setFieldName("int_type");
        int_type.setOffset(Protocol.INT_HEADER_TYPE_OFF);
        int_type.setLength(Protocol.INT_HEADER_TYPE_LEN);

        ArrayList<OFMatch20> match20List = new ArrayList<>();
        match20List.add(int_type);

        OFFlowTable ofFlowTable = new OFFlowTable();
        ofFlowTable.setTableId(tableId);
        ofFlowTable.setTableName(table_name);
        ofFlowTable.setMatchFieldList(match20List);
        ofFlowTable.setMatchFieldNum((byte) 1);
        ofFlowTable.setTableSize(32);
        ofFlowTable.setTableType(OFTableType.OF_MM_TABLE);
        ofFlowTable.setCommand(null);
        ofFlowTable.setKeyLength(Protocol.INT_HEADER_TYPE_LEN);

        FlowTable.Builder flowTable = DefaultFlowTable.builder()
                .withFlowTable(ofFlowTable)
                .forTable(tableId)
                .forDevice(deviceId)
                .fromApp(appId);

        flowTableService.applyFlowTables(flowTable.build());
        log.info("table<{}> applied to device<{}> successfully.", tableId, deviceId.toString());

        return tableId;
    }


    public void remove_pof_flow_table(DeviceId deviceId, byte tableId) {
        flowRuleService.removeFlowRulesById(appId);  // for ovs-pof
        flowTableService.removeFlowTablesByTableId(deviceId, FlowTableId.valueOf(tableId));
        log.info(" remove table from device<{}>  table<{}> successfully.", deviceId.toString(), tableId);
    }


    public void install_pof_output_flow_rule_match_default_ip_at_SRC(DeviceId deviceId, byte tableId, String srcIP, int outport,
                                                           int priority) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID, Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "00000000"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // action
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();
        OFAction action_output = DefaultPofActions.output((short) 0, (short) 0, (short) 0, outport).action();
        actions.add(action_output);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("actions: {}.", actions);

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());
        log.info("match_default_ip_at_SRC: apply to deviceId<{}> tableId<{}>, entryId<{}>", deviceId.toString(), tableId, newFlowEntryId);
    }


    public void install_pof_output_flow_rule_match_default_type_at_INTER_or_SINK(DeviceId deviceId, byte tableId, String intType, int outport,
                                                                     int priority) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.INT_TYPE_ID, Protocol.INT_HEADER_TYPE_OFF, Protocol.INT_HEADER_TYPE_LEN, intType, "0000"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // action
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();
        OFAction action_output = DefaultPofActions.output((short) 0, (short) 0, (short) 0, outport).action();
        actions.add(action_output);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());
        log.info("match_default_type_at_INTER_or_SINK: apply to deviceId<{}> tableId<{}>, entryId=<{}>", deviceId.toString(), tableId, newFlowEntryId);
    }


    /**
     * test no INT scenarios. only output packets.
     * @actions output
     * @param deviceId such as "pof:000000000000000x"
     * @param tableId shoule be table0
     * @param srcIP such as "0a000001", hex str
     * @param outport output port
     * @param priority 12
     */
    public void install_pof_no_int_output_flow_rule(DeviceId deviceId, byte tableId, String srcIP, int outport, int priority) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID, Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "ffffffff"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // action
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();
        OFAction action_output = DefaultPofActions.output((short) 0, (short) 0, (short) 0, outport).action();
        actions.add(action_output);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("action_output: {}.", actions);

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());
        log.info("Test no INT: apply to deviceId<{}> tableId<{}>, entryId=<{}>", deviceId.toString(), tableId, newFlowEntryId);
    }

    /**
     * test per INT scenarios. add INT metadata into packets per packet. adjust metadata type with 'mapInfo'
     * @actions add_int_field + output
     * @param deviceId such as "pof:000000000000000x"
     * @param tableId shoule be table0
     * @param int_type such as "0908", hex str
     * @param outport output port
     * @param priority 12
     * @param mapInfo hex str, one byte. such as '3f'
     */
    public void install_pof_add_int_field_rule_match_type(DeviceId deviceId, byte tableId, String int_type, int outport, int priority, String mapInfo) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.INT_TYPE_ID, Protocol.INT_HEADER_TYPE_OFF, Protocol.INT_HEADER_TYPE_LEN, int_type, "ffff"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // action
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();

        OFAction action_add_int_field = DefaultPofActions.addField(Protocol.INT_FIELD_ID, Protocol.INT_HEADER_DATA_OFF, Protocol.INT_HEADER_TYPE_LEN, mapInfo).action();

        // modify INT-ttl
        OFMatch20 Field_INT_ttl =  new OFMatch20();
        Field_INT_ttl.setFieldName("INT_ttl");
        Field_INT_ttl.setFieldId(Protocol.INT_TTL_ID);
        Field_INT_ttl.setOffset(Protocol.INT_HEADER_TTL_OFF);
        Field_INT_ttl.setLength(Protocol.INT_HEADER_TTL_LEN);

        OFAction action_inc_INT_ttl = DefaultPofActions.modifyField(Field_INT_ttl, 1).action();
        OFAction action_output = DefaultPofActions.output((short) 0, (short) 0, (short) 0, outport).action();

        actions.add(action_add_int_field);    /* add int metadata. */
        actions.add(action_inc_INT_ttl);      /* increment int_ttl field by 1 */
        actions.add(action_output);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("actions: {}.", actions);

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());

        log.info("install_pof_int_field_flow_rule_match_type: apply to deviceId<{}> tableId<{}> entryId<{}>", deviceId.toString(), tableId, newFlowEntryId);
    }


    /**
     *
     * @mapInfo (2B) = 0b'00 00 00 00 00 00 00 00
     *             = x | x | x | x | x | x | fwd_acts | queue_len |
     *               n_bytes | n_packets | bandwidth | egress_time || ingress_time | out_port | in_port | dpid.
     *      if 'mapInfo' == 0xffff, then read 'mapInfo' from packets.
     *      notice, at src node or single node, 'mapInfo' cannot be 0xffff.
     *
     * @sampling_rate_N 1/N sampling method to insert INT header. just apply 'add_int_field' action.
     *      sampling_rate_N define the 'N' to select one in N.
     */
    public void install_pof_add_int_field_rule_match_srcIp(DeviceId deviceId, byte tableId, String srcIp, int outport,
                                                           int priority, String mapInfo, int sampling_rate_N) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID, Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIp, "ffffffff"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // action
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();

        OFAction action_add_int_field = DefaultPofActions.addField(Protocol.INT_FIELD_ID, Protocol.INT_HEADER_DATA_OFF, sampling_rate_N * 8, mapInfo).action();
//        OFAction action_set_eth_type = DefaultPofActions.setField(Protocol.ETH_TYPE_ID, Protocol.ETH_TYPE_OFF, Protocol.ETH_TYPE_LEN, Protocol.INT_TYPE_VAL, Protocol.ETH_TYPE_MASK).action();
        OFAction action_output = DefaultPofActions.output((short) 0, (short) 0, (short) 0, outport).action();

        actions.add(action_add_int_field);
//        actions.add(action_set_eth_type);
        actions.add(action_output);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("actions: {}.", actions);

        // get existed flow rules in flow table. if the srcIp equals, then delete it
        /*Map<Integer, FlowRule> existedFlowRules = new HashMap<>();
        existedFlowRules = flowTableStore.getFlowEntries(deviceId, FlowTableId.valueOf(tableId));
        if(existedFlowRules != null) {
            for(Integer flowEntryId : existedFlowRules.keySet()) {
                if(existedFlowRules.get(flowEntryId).selector().equals(trafficSelector.build())) {
                    flowTableService.removeFlowEntryByEntryId(deviceId, tableId, flowEntryId);
                    log.info("install_pof_add_int_field_rule_match_srcIp: remove flow entry, deviceId<{}> tableId<{}> entryId<{}>",
                            deviceId.toString(), tableId, flowEntryId);
                }
            }
        }*/

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());

        log.info("install_pof_int_field_flow_rule_match_srcIP: apply to deviceId<{}> tableId<{}> entryId<{}>", deviceId.toString(), tableId, newFlowEntryId);
    }


    /**
     * if outport = CONTROLLER, then it will packet_in to controller
     */
    public void install_pof_output_flow_rule(DeviceId deviceId, byte tableId, String srcIP, int outport, int priority) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID , Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "ffffffff"));
//        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID , Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "00000000"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // action
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();

        OFAction action_output = DefaultPofActions.output((short) 0, (short) 0, (short) 0, outport).action();
        actions.add(action_output);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("action_output: {}.", actions);

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                                                   .forDevice(deviceId)
                                                   .forTable(tableId)
                                                   .withSelector(trafficSelector.build())
                                                   .withTreatment(trafficTreamt.build())
                                                   .withPriority(priority)
                                                   .withCookie(newFlowEntryId)
                                                   .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());

        log.info("installOutputFlowRule: apply to deviceId<{}> tableId<{}>, entryId=<{}>", deviceId.toString(), tableId, newFlowEntryId);
    }


    public void install_pof_set_field_rule(DeviceId deviceId, byte tableId, String srcIP, int outport, int priority) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID , Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "ffffffff"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // action
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();
        OFAction action_set_dstIp = DefaultPofActions.setField(Protocol.DIP_ID, Protocol.IPV4_DIP_OFF, Protocol.IPV4_DIP_LEN, "0a020202", "ffffffff").action();
        OFAction action_set_srcIp = DefaultPofActions.setField(Protocol.SIP_ID , Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, "0a0a0a0a", "ffffffff").action();
//        OFAction action_set_ttl = DefaultPofActions.setField(Protocol.TTL_ID, Protocol.IPV4_TTL_OFF, Protocol.IPV4_TTL_LEN, "66", "ff").action();
        OFAction action_output = DefaultPofActions.output((short) 0, (short) 0, (short) 0, outport).action();
//        actions.add(action_set_dstIp);
        actions.add(action_set_srcIp);
//        actions.add(action_set_ttl);
        actions.add(action_output);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("action_set_field: {}.", actions);

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());

        log.info("installSetFieldFlowRule: apply to deviceId<{}> tableId<{}>", deviceId.toString(), tableId);
    }


    public void install_pof_add_static_field_rule(DeviceId deviceId, byte tableId, String srcIP, int outport, int priority) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID , Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "ffffffff"));
//        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID , Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "00000000"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // action
        short field_id1 = 17;
        short field_id2 = 18;
        short field_id3 = 19;
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();
        OFAction action_add_field1 = DefaultPofActions.addField(field_id1, (short) 272, (short) 16, "0908").action();
//        OFAction action_add_field2 = DefaultPofActions.addField(field_id2, (short) 272, (short) 16, "1918").action();
//        OFAction action_add_field3 = DefaultPofActions.addField(field_id3, (short) 272, (short) 16, "2928").action();
        OFAction action_output = DefaultPofActions.output((short) 0, (short) 0, (short) 0, outport).action();
        actions.add(action_add_field1);
//        actions.add(action_add_field2);
//        actions.add(action_add_field3);
        actions.add(action_output);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("action_add_field: {}.", actions);

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());

        log.info("installAddFieldFlowRule: apply to deviceId<{}> tableId<{}>", deviceId.toString(), tableId);
    }


    public void install_pof_add_dynamic_field_rule(DeviceId deviceId, byte tableId, String srcIP, int outport, int priority) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID , Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "ffffffff"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // action
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();

        /* 0b'00 00 00 00 = x | x | bandwidth | egress_time || ingress_time | out_port | in_port | dpid.
        * if 'filed_value' == 0xff, then read 'mapInfo' from packets.
        */
        OFAction action_add_field1 = DefaultPofActions.addField(Protocol.INT_FIELD_ID, Protocol.INT_HEADER_DATA_OFF, (short) 16, "0001").action();
        OFAction action_add_func_field1 = DefaultPofActions.addField(Protocol.INT_DPID_ID, Protocol.INT_DATA_DPID_END_OFF, Protocol.INT_DATA_DPID_END_LEN, funcByteHexStr(deviceId)).action();

        OFAction action_output = DefaultPofActions.output((short) 0, (short) 0, (short) 0, outport).action();

        actions.add(action_add_field1);
//        actions.add(action_add_func_field1);
        actions.add(action_output);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("action_add_field: {}.", actions);

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());

        log.info("install_pof_dynamic_field_flow_rule: apply to deviceId<{}> tableId<{}>", deviceId.toString(), tableId);
    }


    public void install_pof_delete_field_rule(DeviceId deviceId, byte tableId, String srcIP, int outport, int priority) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID , Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "ffffffff"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // action
        short field_id1 = 17;
        short offset = 272;
        int len = 16;
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();
        OFAction action_delete_field = DefaultPofActions.deleteField(offset, len).action();
//        OFAction action_delete_field1 = DefaultPofActions.deleteField((short) 272, (short) 16).action();
        OFAction action_output = DefaultPofActions.output((short) 0, (short) 0, (short) 0, outport).action();
        actions.add(action_delete_field);
//        actions.add(action_delete_field1);
//        actions.add(action_delete_field1);
        actions.add(action_output);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("action_delete_field: {}.", actions);

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());

        log.info("installDeleteFieldFlowRule: apply to deviceId<{}> tableId<{}>", deviceId.toString(), tableId);
    }


    /* if 'len' = -1, then delete INT data according to its 'mapInfo', 'offset' defines the start location of INT_header */
    public void install_pof_delete_int_field_rule(DeviceId deviceId, byte tableId, String srcIP, int outport, int priority) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID , Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "ffffffff"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // action
        short del_int_off = Protocol.INT_HEADER_BASE;
        short del_int_len = Protocol.INT_FIELD_ID;   // means sw read 'mapInfo' from pkts and get the real deleted len.
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();
        OFAction action_delete_field = DefaultPofActions.deleteField(del_int_off, del_int_len).action();
        OFAction action_output = DefaultPofActions.output((short) 0, (short) 0, (short) 0, outport).action();
        actions.add(action_delete_field);
        actions.add(action_output);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("action_delete_field: {}.", actions);

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());

        log.info("installDeleteFieldFlowRule: apply to deviceId<{}> tableId<{}>", deviceId.toString(), tableId);
    }


    public void install_pof_modify_field_rule(DeviceId deviceId, byte tableId, String srcIP, int outport, int priority) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID , Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "ffffffff"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // modify ttl
        OFMatch20 FIELD_TTL = new OFMatch20();
        FIELD_TTL.setFieldName("TTL");
        FIELD_TTL.setFieldId(Protocol.TTL_ID);
        FIELD_TTL.setOffset(Protocol.IPV4_TTL_OFF);
        FIELD_TTL.setLength(Protocol.IPV4_TTL_LEN);

        // modify srcIp's last byte
        OFMatch20 FIELD_SIP = new OFMatch20();
        FIELD_SIP.setFieldName("SIP");
        FIELD_SIP.setFieldId(Protocol.SIP_ID);
        FIELD_SIP.setOffset((short) (Protocol.IPV4_SIP_OFF + 24));
        FIELD_SIP.setLength(Protocol.IPV4_SIP_LEN);

        // modify dstIp's last byte
        OFMatch20 FIELD_DIP = new OFMatch20();
        FIELD_DIP.setFieldName("DIP");
        FIELD_DIP.setFieldId(Protocol.DIP_ID);
        FIELD_DIP.setOffset((short) (Protocol.IPV4_DIP_OFF + 24));
        FIELD_DIP.setLength(Protocol.IPV4_DIP_LEN);

        // action
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();
        OFAction action_modify_ttl = DefaultPofActions.modifyField(FIELD_TTL, 65535).action();
//        OFAction action_modify_dip = DefaultPofActions.modifyField(FIELD_DIP, 12).action();
//        OFAction action_modify_sip = DefaultPofActions.modifyField(FIELD_SIP, 12).action();
        OFAction action_output = DefaultPofActions.output((short) 0, (short) 0, (short) 0, outport).action();
        OFAction action_add_field1 = DefaultPofActions.addField((short) 16, (short) 272, (short) 64, "0102030405060708").action();
        actions.add(action_add_field1);
        actions.add(action_modify_ttl);
//        actions.add(action_modify_dip);
//        actions.add(action_modify_sip);
        actions.add(action_output);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("action_modify_field: {}.", actions);

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());

        log.info("installModifyFieldFlowRule: apply to deviceId<{}> tableId<{}>", deviceId.toString(), tableId);
    }

    /* controller indicates 'fwd_acts' according to 'field_id'. */
    public void install_pof_FWD_MOD_FIELD_rule_match_type(DeviceId deviceId, byte tableId, String int_type, int outport, int priority, String mapInfo) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.INT_TYPE_ID, Protocol.INT_HEADER_TYPE_OFF, Protocol.INT_HEADER_TYPE_LEN, int_type, "ffff"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // modify INT-ttl
        OFMatch20 Field_INT_ttl =  new OFMatch20();
        Field_INT_ttl.setFieldName("INT_ttl");
        Field_INT_ttl.setFieldId(Protocol.INT_TTL_ID);
        Field_INT_ttl.setOffset(Protocol.INT_HEADER_TTL_OFF);
        Field_INT_ttl.setLength(Protocol.INT_HEADER_TTL_LEN);
        OFAction action_inc_INT_ttl = DefaultPofActions.modifyField(Field_INT_ttl, 1).action();

        // FWD_MOD_SIP_FIELD: modify last byte
        OFMatch20 FIELD_SIP = new OFMatch20();
        FIELD_SIP.setFieldName("FWD_MOD_SIP");
        FIELD_SIP.setFieldId(Protocol.FWD_MOD_SIP_FIELD_ID);
        FIELD_SIP.setOffset(Protocol.IPV4_SIP_OFF);
        FIELD_SIP.setLength(Protocol.IPV4_SIP_LEN);
        OFAction action_fwd_mod_sip = DefaultPofActions.modifyField(FIELD_SIP, 10).action();

        // FWD_MOD_DIP_FIELD: modify last byte
        OFMatch20 FIELD_DIP = new OFMatch20();
        FIELD_DIP.setFieldName("FWD_MOD_DIP");
        FIELD_DIP.setFieldId(Protocol.FWD_MOD_DIP_FIELD_ID);
        FIELD_DIP.setOffset(Protocol.IPV4_DIP_OFF);
        FIELD_DIP.setLength(Protocol.IPV4_DIP_LEN);
        OFAction action_fwd_mod_dip = DefaultPofActions.modifyField(FIELD_DIP, 11).action();

        // FWD_MOD_SMAC_FIELD: modify last byte
        OFMatch20 FIELD_SMAC = new OFMatch20();
        FIELD_SMAC.setFieldName("FWD_MOD_SMAC");
        FIELD_SMAC.setFieldId(Protocol.FWD_MOD_SMAC_FIELD_ID);
        FIELD_SMAC.setOffset(Protocol.ETH_SMAC_OFF);
        FIELD_SMAC.setLength(Protocol.ETH_SMAC_LEN);
        OFAction action_fwd_mod_smac = DefaultPofActions.modifyField(FIELD_SMAC, 12).action();

        // FWD_MOD_DMAC_FIELD: modify last byte
        OFMatch20 FIELD_DMAC = new OFMatch20();
        FIELD_DMAC.setFieldName("FWD_MOD_DMAC");
        FIELD_DMAC.setFieldId(Protocol.FWD_MOD_DMAC_FIELD_ID);
        FIELD_DMAC.setOffset(Protocol.ETH_DMAC_OFF);
        FIELD_DMAC.setLength(Protocol.ETH_DMAC_LEN);
        OFAction action_fwd_mod_dmac = DefaultPofActions.modifyField(FIELD_DMAC, 1).action();

        // FWD_ADD_INT_HDR_FIELD_ID: add int header
        OFAction action_add_int_field = DefaultPofActions.addField(Protocol.FWD_ADD_INT_HDR_FIELD_ID,
                Protocol.INT_HEADER_DATA_OFF, Protocol.INT_HEADER_TYPE_LEN, mapInfo).action();


        // action
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();
        OFAction action_output = DefaultPofActions.output((short) 0, (short) 0, (short) 0, outport).action();

//        actions.add(action_fwd_mod_sip);
//        actions.add(action_fwd_mod_dip);
//        actions.add(action_fwd_mod_smac);
        actions.add(action_fwd_mod_dmac);
        actions.add(action_add_int_field);
        actions.add(action_inc_INT_ttl);
        actions.add(action_output);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("actions: {}.", actions);

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());

        log.info("installModifyFieldFlowRule: apply to deviceId<{}> tableId<{}> entryId<{}>", deviceId.toString(), tableId, newFlowEntryId);
    }


    /* controller indicates 'fwd_acts' according to 'field_id'. */
    public void install_pof_FWD_MOD_FIELD_rule_match_srcIP(DeviceId deviceId, byte tableId, String srcIP, int outport, int priority, String mapInfo, int sampling_rate_N) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID , Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "ffffffff"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // modify INT-ttl
        OFMatch20 Field_INT_ttl =  new OFMatch20();
        Field_INT_ttl.setFieldName("INT_ttl");
        Field_INT_ttl.setFieldId(Protocol.INT_TTL_ID);
        Field_INT_ttl.setOffset(Protocol.INT_HEADER_TTL_OFF);
        Field_INT_ttl.setLength(Protocol.INT_HEADER_TTL_LEN);
        OFAction action_inc_INT_ttl = DefaultPofActions.modifyField(Field_INT_ttl, 1).action();

        // FWD_MOD_SIP_FIELD: modify last byte
        OFMatch20 FIELD_SIP = new OFMatch20();
        FIELD_SIP.setFieldName("FWD_MOD_SIP");
        FIELD_SIP.setFieldId(Protocol.FWD_MOD_SIP_FIELD_ID);
        FIELD_SIP.setOffset(Protocol.IPV4_SIP_OFF);
        FIELD_SIP.setLength(Protocol.IPV4_SIP_LEN);
        OFAction action_fwd_mod_sip = DefaultPofActions.modifyField(FIELD_SIP, 10).action();

        // FWD_MOD_DIP_FIELD: modify last byte
        OFMatch20 FIELD_DIP = new OFMatch20();
        FIELD_DIP.setFieldName("FWD_MOD_DIP");
        FIELD_DIP.setFieldId(Protocol.FWD_MOD_DIP_FIELD_ID);
        FIELD_DIP.setOffset(Protocol.IPV4_DIP_OFF);
        FIELD_DIP.setLength(Protocol.IPV4_DIP_LEN);
        OFAction action_fwd_mod_dip = DefaultPofActions.modifyField(FIELD_DIP, 11).action();

        // FWD_MOD_SMAC_FIELD: modify last byte
        OFMatch20 FIELD_SMAC = new OFMatch20();
        FIELD_SMAC.setFieldName("FWD_MOD_SMAC");
        FIELD_SMAC.setFieldId(Protocol.FWD_MOD_SMAC_FIELD_ID);
        FIELD_SMAC.setOffset(Protocol.ETH_SMAC_OFF);
        FIELD_SMAC.setLength(Protocol.ETH_SMAC_LEN);
        OFAction action_fwd_mod_smac = DefaultPofActions.modifyField(FIELD_SMAC, 12).action();

        // FWD_MOD_DMAC_FIELD: modify last byte
        OFMatch20 FIELD_DMAC = new OFMatch20();
        FIELD_DMAC.setFieldName("FWD_MOD_DMAC");
        FIELD_DMAC.setFieldId(Protocol.FWD_MOD_DMAC_FIELD_ID);
        FIELD_DMAC.setOffset(Protocol.ETH_DMAC_OFF);
        FIELD_DMAC.setLength(Protocol.ETH_DMAC_LEN);
        OFAction action_fwd_mod_dmac = DefaultPofActions.modifyField(FIELD_DMAC, 13).action();

        // FWD_ADD_INT_HDR_FIELD_ID: add int header
        OFAction action_add_int_field = DefaultPofActions.addField(Protocol.FWD_ADD_INT_HDR_FIELD_ID,
                                            Protocol.INT_HEADER_DATA_OFF, sampling_rate_N * 8, mapInfo).action();


        // action
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();
        OFAction action_output = DefaultPofActions.output((short) 0, (short) 0, (short) 0, outport).action();

//        actions.add(action_fwd_mod_sip);
//        actions.add(action_fwd_mod_dip);
//        actions.add(action_fwd_mod_smac);
//        actions.add(action_fwd_mod_dmac);
        actions.add(action_add_int_field);
//        actions.add(action_inc_INT_ttl);    // first_hop does not execute it
        actions.add(action_output);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("actions: {}.", actions);

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());

        log.info("installModifyFieldFlowRule: apply to deviceId<{}> tableId<{}> entryId<{}>", deviceId.toString(), tableId, newFlowEntryId);
    }



    public void installDropFlowRule(DeviceId deviceId, byte tableId, String srcIP, int outport) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID , Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "ffffffff"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // action
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();
        OFAction action_drop = DefaultPofActions.drop(1).action();
        actions.add(action_drop);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("action_drop: {}.", actions);

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(1)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());

        log.info("installDropFlowRule: apply to deviceId<{}> tableId<{}>", deviceId.toString(), tableId);
    }


    public void install_pof_group_rule_match_srcIp(DeviceId deviceId, byte tableId, String srcIP, int groupId, int priority) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID , Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "ffffffff"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // action
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();
        OFAction action_group = DefaultPofActions.group(groupId).action();
        actions.add(action_group);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());
        log.info("group_rule_match_srcIp: apply to deviceId<{}> tableId<{}> entryId<{}>", deviceId.toString(), tableId, newFlowEntryId);
    }


    public void install_pof_group_rule_match_type(DeviceId deviceId, byte tableId, String int_type, int groupId, int priority) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.INT_TYPE_ID, Protocol.INT_HEADER_TYPE_OFF, Protocol.INT_HEADER_TYPE_LEN, int_type, "ffff"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // action
        TrafficTreatment.Builder trafficTreamt = DefaultTrafficTreatment.builder();
        List<OFAction> actions = new ArrayList<>();
        OFAction action_group = DefaultPofActions.group(groupId).action();
        actions.add(action_group);
        trafficTreamt.add(DefaultPofInstructions.applyActions(actions));
        log.info("actions: {}.", actions);

        // apply
        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreamt.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());
        log.info("group_rule_match_type: apply to deviceId<{}> tableId<{}> entryId<{}>", deviceId.toString(), tableId, newFlowEntryId);
    }


    /* sel_group at sw1 (src_node), bucket2 do INT operation. */
    public void install_pof_select_group_rule(DeviceId deviceId, byte tableId, int out_port1, int out_port2, String srcIP,
                                              String key_str, int groupId, int priority,
                                              short weight1, short weight2, String mapInfo) {
        GroupId select_group_id = new GroupId(groupId);

        byte[] keyData = key_str.getBytes();
        final GroupKey key = new DefaultGroupKey(keyData);

        /* modify SIP: make this flow into 2 flows. otherwise, match error at next node. Only used at src.
         *             because dpdk->rss_hash will hash src_ip and dst_ip and see them as one flow. We insert
         *             INT_HEADER behind IPv4.dst, will mis-match (encounter match-only-one-flow again)
         *             at next node.
         */
        short int_field_id = -1;
        OFMatch20 Field_SIP =  new OFMatch20();
        Field_SIP.setFieldName("SIP_B3");
        Field_SIP.setFieldId(Protocol.SIP_ID);
        Field_SIP.setOffset((short) (Protocol.IPV4_SIP_OFF + 16));
        Field_SIP.setLength(Protocol.IPV4_SIP_LEN);
        OFAction action_inc_SIP = DefaultPofActions.modifyField(Field_SIP, 1).action();

        // modify INT-ttl
        OFMatch20 Field_INT_ttl =  new OFMatch20();
        Field_INT_ttl.setFieldName("INT_ttl");
        Field_INT_ttl.setFieldId(Protocol.INT_TTL_ID);
        Field_INT_ttl.setOffset(Protocol.INT_HEADER_TTL_OFF);
        Field_INT_ttl.setLength(Protocol.INT_HEADER_TTL_LEN);
        OFAction action_inc_INT_ttl = DefaultPofActions.modifyField(Field_INT_ttl, 1).action();

        // bucket1: action = output
        TrafficTreatment.Builder trafficTreatment_bucket1 = DefaultTrafficTreatment.builder();
        List<OFAction> actions_bucket1 = new ArrayList<>();
        OFAction action_output1 = DefaultPofActions.output((short) 0, (short) 0, (short) 0, out_port1).action();
        actions_bucket1.add(action_inc_SIP);   // must contain this action, make 'rss_hash' different
        actions_bucket1.add(action_output1);
        trafficTreatment_bucket1.add(DefaultPofInstructions.applyActions(actions_bucket1));

        // bucket1: weight1 -- output
        GroupBucket bucket1 = DefaultGroupBucket.createSelectGroupBucket(trafficTreatment_bucket1.build(), weight1);


        // bucket2: action = add_int_field + output, inc_int_ttl at data plane (src node).
        TrafficTreatment.Builder trafficTreatment_bucket2 = DefaultTrafficTreatment.builder();
        List<OFAction> actions_bucket2 = new ArrayList<>();
        OFAction action_add_int_field = DefaultPofActions.addField(int_field_id, Protocol.INT_HEADER_BASE, (short) 24, mapInfo).action();
        OFAction action_output2 = DefaultPofActions.output((short) 0, (short) 0, (short) 0, out_port2).action();
        actions_bucket2.add(action_add_int_field);

//        actions_bucket2.add(action_inc_INT_ttl);   // no need inc_INT_ttl here, we directly set it at src node.
        actions_bucket2.add(action_output2);
        trafficTreatment_bucket2.add(DefaultPofInstructions.applyActions(actions_bucket2));

        // bucket2: weight2 -- int-operation
        GroupBucket bucket2 = DefaultGroupBucket.createSelectGroupBucket(trafficTreatment_bucket2.build(), weight2);

        // buckets
        GroupBuckets select_group_buckets = new GroupBuckets(ImmutableList.of(bucket1, bucket2));

        // apply
        DefaultGroupDescription select_group = new DefaultGroupDescription(deviceId,
                GroupDescription.Type.SELECT, select_group_buckets, key, select_group_id.id(), appId);

        groupService.addGroup(select_group);
        log.info("Add select group table to deviceId<{}>, groupId<{}>, w1:w2={}:{}", deviceId.toString(), groupId, weight1, weight2);
    }


    /* moddify sel_group Mod at sw1 (src_node), bucket2 do INT operation. */
    public void install_mod_pof_select_group_rule(DeviceId deviceId, byte tableId, int out_port1, int out_port2, String srcIP,
                                              String old_key_str, String new_key_str, int groupId, int priority,
                                              short weight1, short weight2, String mapInfo) {
        GroupId select_group_id = new GroupId(groupId);

        byte[] keyData = old_key_str.getBytes();
        final GroupKey old_key = new DefaultGroupKey(keyData);

        /* modify SIP: make this flow into 2 flows. otherwise, match error at next node. Only used at src.
         *             because dpdk->rss_hash will hash src_ip and dst_ip and see them as one flow. We insert
         *             INT_HEADER behind IPv4.dst, will mis-match (encounter match-only-one-flow again)
         *             at next node.
         */
        short int_field_id = -1;
        OFMatch20 Field_SIP =  new OFMatch20();
        Field_SIP.setFieldName("SIP_B3");
        Field_SIP.setFieldId(Protocol.SIP_ID);
        Field_SIP.setOffset((short) (Protocol.IPV4_SIP_OFF + 16));
        Field_SIP.setLength(Protocol.IPV4_SIP_LEN);
        OFAction action_inc_SIP = DefaultPofActions.modifyField(Field_SIP, 1).action();

        // modify INT-ttl
        OFMatch20 Field_INT_ttl =  new OFMatch20();
        Field_INT_ttl.setFieldName("INT_ttl");
        Field_INT_ttl.setFieldId(Protocol.INT_TTL_ID);
        Field_INT_ttl.setOffset(Protocol.INT_HEADER_TTL_OFF);
        Field_INT_ttl.setLength(Protocol.INT_HEADER_TTL_LEN);
        OFAction action_inc_INT_ttl = DefaultPofActions.modifyField(Field_INT_ttl, 1).action();

        // bucket1: action = output
        TrafficTreatment.Builder trafficTreatment_bucket1 = DefaultTrafficTreatment.builder();
        List<OFAction> actions_bucket1 = new ArrayList<>();
        OFAction action_output1 = DefaultPofActions.output((short) 0, (short) 0, (short) 0, out_port1).action();
        actions_bucket1.add(action_inc_SIP);   // must contain this action, make 'rss_hash' different
        actions_bucket1.add(action_output1);
        trafficTreatment_bucket1.add(DefaultPofInstructions.applyActions(actions_bucket1));

        // bucket1: weight1 -- output
        GroupBucket bucket1 = DefaultGroupBucket.createSelectGroupBucket(trafficTreatment_bucket1.build(), weight1);

        // bucket2: action = add_int_field + output, inc_int_ttl at data plane (src node).
        TrafficTreatment.Builder trafficTreatment_bucket2 = DefaultTrafficTreatment.builder();
        List<OFAction> actions_bucket2 = new ArrayList<>();
        OFAction action_add_int_field = DefaultPofActions.addField(Protocol.INT_FIELD_ID, Protocol.INT_HEADER_BASE, (short) 24, mapInfo).action();
        OFAction action_output2 = DefaultPofActions.output((short) 0, (short) 0, (short) 0, out_port2).action();
        actions_bucket2.add(action_add_int_field);

//        actions_bucket2.add(action_inc_INT_ttl);   // no need inc_INT_ttl here, we directly set it at src node.
        actions_bucket2.add(action_output2);
        trafficTreatment_bucket2.add(DefaultPofInstructions.applyActions(actions_bucket2));

        // bucket2: weight2 -- int-operation
        GroupBucket bucket2 = DefaultGroupBucket.createSelectGroupBucket(trafficTreatment_bucket2.build(), weight2);

        // buckets
        GroupBuckets select_group_buckets = new GroupBuckets(ImmutableList.of(bucket1, bucket2));

        // apply
        DefaultGroupDescription select_group = new DefaultGroupDescription(deviceId,
                GroupDescription.Type.SELECT, select_group_buckets, old_key, select_group_id.id(), appId);

        /* this is modify a exsisting group table. */
        byte[] new_keyData = new_key_str.getBytes();
        final GroupKey new_key = new DefaultGroupKey(new_keyData);
        GroupBuckets new_buckets = new GroupBuckets(ImmutableList.of(bucket1, bucket2));
        groupService.setBucketsForGroup(deviceId, old_key, new_buckets, new_key, appId);
        log.info("Modify select group table to deviceId<{}>, groupId<{}>, w1:w2={}:{}", deviceId.toString(), groupId, weight1, weight2);
    }


    /* sel_group at sw2, all buckets do INT operation. */
    public void install_pof_select_group_rule_at_sw2(DeviceId deviceId, byte tableId, int out_port1, int out_port2, String srcIP,
                                              String key_str, int groupId, int priority,
                                              short weight1, short weight2, String mapInfo) {
        GroupId select_group_id = new GroupId(groupId);

        byte[] keyData = key_str.getBytes();
        final GroupKey key = new DefaultGroupKey(keyData);

        // modify INT-ttl
        short int_field_id = -1;
        OFMatch20 Field_INT_ttl =  new OFMatch20();
        Field_INT_ttl.setFieldName("INT_ttl");
        Field_INT_ttl.setFieldId(Protocol.INT_TTL_ID);
        Field_INT_ttl.setOffset(Protocol.INT_HEADER_TTL_OFF);
        Field_INT_ttl.setLength(Protocol.INT_HEADER_TTL_LEN);
        OFAction action_inc_INT_ttl = DefaultPofActions.modifyField(Field_INT_ttl, 1).action();
        OFAction action_add_func_field = DefaultPofActions.addField(Protocol.INT_DPID_ID, Protocol.INT_DATA_DPID_END_OFF, Protocol.INT_DATA_DPID_END_LEN, funcByteHexStr(deviceId)).action(); // for path revalidation

        // bucket1: action = add_int_field + inc_int_ttl + output:out_port1
        TrafficTreatment.Builder trafficTreatment_bucket1 = DefaultTrafficTreatment.builder();
        List<OFAction> actions_bucket1 = new ArrayList<>();
        OFAction action_add_int_field = DefaultPofActions.addField(Protocol.INT_FIELD_ID, Protocol.INT_HEADER_DATA_OFF, (short) 16, mapInfo).action();
        OFAction action_output1 = DefaultPofActions.output((short) 0, (short) 0, (short) 0, out_port1).action();
        actions_bucket1.add(action_add_int_field);

        actions_bucket1.add(action_inc_INT_ttl);
        actions_bucket1.add(action_output1);
        trafficTreatment_bucket1.add(DefaultPofInstructions.applyActions(actions_bucket1));

        // bucket1: weight1 -- output
        GroupBucket bucket1 = DefaultGroupBucket.createSelectGroupBucket(trafficTreatment_bucket1.build(), weight1);

        // bucket2: action = add_int_field + inc_int_ttl + output:out_port2
        TrafficTreatment.Builder trafficTreatment_bucket2 = DefaultTrafficTreatment.builder();
        List<OFAction> actions_bucket2 = new ArrayList<>();
//        OFAction action_add_int_field2 = DefaultPofActions.addField(Protocol.INT_FIELD_ID, Protocol.INT_HEADER_DATA_OFF, (short) 16, mapInfo).action();
        OFAction action_output2 = DefaultPofActions.output((short) 0, (short) 0, (short) 0, out_port2).action();
        actions_bucket2.add(action_add_int_field);

        actions_bucket2.add(action_inc_INT_ttl);
        actions_bucket2.add(action_output2);
        trafficTreatment_bucket2.add(DefaultPofInstructions.applyActions(actions_bucket2));

        // bucket2: weight2 -- int-operation
        GroupBucket bucket2 = DefaultGroupBucket.createSelectGroupBucket(trafficTreatment_bucket2.build(), weight2);

        // buckets
        GroupBuckets select_group_buckets = new GroupBuckets(ImmutableList.of(bucket1, bucket2));

        // apply
        DefaultGroupDescription select_group = new DefaultGroupDescription(deviceId,
                GroupDescription.Type.SELECT, select_group_buckets, key, select_group_id.id(), appId);

        groupService.addGroup(select_group);
        log.info("Add select group table at sw2, deviceId<{}> groupId<{}>", deviceId.toString(), groupId);
    }


    /* sel_group at sw2, all buckets do INT operation. */
    public void install_mod_pof_select_group_rule_at_sw2(DeviceId deviceId, byte tableId, int out_port1, int out_port2, String srcIP,
                                                     String old_key_str, String new_key_str, int groupId, int priority,
                                                     short weight1, short weight2, String mapInfo) {
        GroupId select_group_id = new GroupId(groupId);

        byte[] keyData = old_key_str.getBytes();
        final GroupKey old_key = new DefaultGroupKey(keyData);

        // modify INT-ttl
        short int_field_id = -1;
        OFMatch20 Field_INT_ttl =  new OFMatch20();
        Field_INT_ttl.setFieldName("INT_ttl");
        Field_INT_ttl.setFieldId(Protocol.INT_TTL_ID);
        Field_INT_ttl.setOffset(Protocol.INT_HEADER_TTL_OFF);
        Field_INT_ttl.setLength(Protocol.INT_HEADER_TTL_LEN);
        OFAction action_inc_INT_ttl = DefaultPofActions.modifyField(Field_INT_ttl, 1).action();
        OFAction action_add_func_field = DefaultPofActions.addField(Protocol.INT_DPID_ID, Protocol.INT_DATA_DPID_END_OFF, Protocol.INT_DATA_DPID_END_LEN, funcByteHexStr(deviceId)).action(); // for path revalidation

        // bucket1: action = add_int_field + inc_int_ttl + output:out_port1
        TrafficTreatment.Builder trafficTreatment_bucket1 = DefaultTrafficTreatment.builder();
        List<OFAction> actions_bucket1 = new ArrayList<>();
        OFAction action_add_int_field = DefaultPofActions.addField(Protocol.INT_FIELD_ID, Protocol.INT_HEADER_DATA_OFF, (short) 16, mapInfo).action();
        OFAction action_output1 = DefaultPofActions.output((short) 0, (short) 0, (short) 0, out_port1).action();
        actions_bucket1.add(action_add_int_field);

        actions_bucket1.add(action_inc_INT_ttl);
        actions_bucket1.add(action_output1);
        trafficTreatment_bucket1.add(DefaultPofInstructions.applyActions(actions_bucket1));

        // bucket1: weight1 -- output
        GroupBucket bucket1 = DefaultGroupBucket.createSelectGroupBucket(trafficTreatment_bucket1.build(), weight1);

        // bucket2: action = add_int_field + inc_int_ttl + output:out_port2
        TrafficTreatment.Builder trafficTreatment_bucket2 = DefaultTrafficTreatment.builder();
        List<OFAction> actions_bucket2 = new ArrayList<>();
//        OFAction action_add_int_field2 = DefaultPofActions.addField(Protocol.INT_FIELD_ID, Protocol.INT_HEADER_DATA_OFF, (short) 16, mapInfo).action();
        OFAction action_output2 = DefaultPofActions.output((short) 0, (short) 0, (short) 0, out_port2).action();
        actions_bucket2.add(action_add_int_field);

        actions_bucket2.add(action_inc_INT_ttl);
        actions_bucket2.add(action_output2);
        trafficTreatment_bucket2.add(DefaultPofInstructions.applyActions(actions_bucket2));

        // bucket2: weight2 -- int-operation
        GroupBucket bucket2 = DefaultGroupBucket.createSelectGroupBucket(trafficTreatment_bucket2.build(), weight2);

        // buckets
        GroupBuckets select_group_buckets = new GroupBuckets(ImmutableList.of(bucket1, bucket2));

        // apply
        DefaultGroupDescription select_group = new DefaultGroupDescription(deviceId,
                GroupDescription.Type.SELECT, select_group_buckets, old_key, select_group_id.id(), appId);

        /* this is add a new group table. */
//        groupService.addGroup(select_group);
//        log.info("Add select group table to deviceId<{}>, groupId<{}>, w1:w2={}:{}", deviceId.toString(), groupId, weight1, weight2);

        /* this is modify a exsisting group table. */
        byte[] new_keyData = new_key_str.getBytes();
        final GroupKey new_key = new DefaultGroupKey(new_keyData);
        GroupBuckets new_buckets = new GroupBuckets(ImmutableList.of(bucket1, bucket2));
        groupService.setBucketsForGroup(deviceId, old_key, new_buckets, new_key, appId);
        log.info("Modify select group table at sw2 to deviceId<{}>, groupId<{}>, w1:w2={}:{}", deviceId.toString(), groupId, weight1, weight2);
    }


    public void install_pof_all_group_rule_match_type(DeviceId deviceId, byte tableId, String int_type,String key_str, int groupId,
                                           int priority, int usr_port, int collect_port, String mapInfo) {
        GroupId select_group_id = new GroupId(groupId);

        byte[] keyData = key_str.getBytes();
        final GroupKey key = new DefaultGroupKey(keyData);

        // bucket1: add_int_field + output
        TrafficTreatment.Builder trafficTreatment_bucket1 = DefaultTrafficTreatment.builder();
        List<OFAction> actions_bucket1 = new ArrayList<>();

        // add-int-field
        OFAction action_add_int_field = DefaultPofActions.addField(Protocol.INT_FIELD_ID, Protocol.INT_HEADER_DATA_OFF,
                                        Protocol.INT_HEADER_TYPE_LEN, mapInfo).action(); // 'mapInfo' should be 0xffff

        // set eth-type back to 0x0800
        // add-int-field
//        OFAction action_set_eth_type = DefaultPofActions.setField(Protocol.ETH_TYPE_ID, Protocol.ETH_TYPE_OFF,
//                Protocol.ETH_TYPE_LEN, Protocol.ETH_TYPE_VAL, Protocol.ETH_TYPE_MASK).action(); // 'mapInfo' should be 0xffff

        // modify INT-ttl
        OFMatch20 Field_INT_ttl =  new OFMatch20();
        Field_INT_ttl.setFieldName("INT_ttl");
        Field_INT_ttl.setFieldId(Protocol.INT_TTL_ID);
        Field_INT_ttl.setOffset(Protocol.INT_HEADER_TTL_OFF);
        Field_INT_ttl.setLength(Protocol.INT_HEADER_TTL_LEN);
        OFAction action_inc_INT_ttl = DefaultPofActions.modifyField(Field_INT_ttl, 1).action();

        OFAction action_output1 = DefaultPofActions.output((short) 0, (short) 0, (short) 0, collect_port).action();
        actions_bucket1.add(action_add_int_field);    /* add int metadata. */
        actions_bucket1.add(action_inc_INT_ttl);      /* increment int_ttl field by 1 */
//        actions_bucket1.add(action_set_eth_type);
        actions_bucket1.add(action_output1);
        trafficTreatment_bucket1.add(DefaultPofInstructions.applyActions(actions_bucket1));
        log.info("actions_bucket1: {}.", actions_bucket1);

        // bucket1: weight
        GroupBucket bucket1 = DefaultGroupBucket.createAllGroupBucket(trafficTreatment_bucket1.build());

        // bucket2: action: del_int_field + output
        short del_int_off = Protocol.INT_HEADER_BASE;
        short del_int_len = Protocol.INT_FIELD_ID;   // means sw read 'mapInfo' from pkts and get the real deleted len.
        TrafficTreatment.Builder trafficTreatment_bucket2 = DefaultTrafficTreatment.builder();
        List<OFAction> actions_bucket2 = new ArrayList<>();
        OFAction action_del_int_field = DefaultPofActions.deleteField(del_int_off, del_int_len).action();
        OFAction action_output2 = DefaultPofActions.output((short) 0, (short) 0, (short) 0, usr_port).action();
        actions_bucket2.add(action_del_int_field);
        actions_bucket2.add(action_output2);
        trafficTreatment_bucket2.add(DefaultPofInstructions.applyActions(actions_bucket2));
        log.info("actions_bucket2: {}.", actions_bucket2);

        // bucket2: weight
        GroupBucket bucket2 = DefaultGroupBucket.createAllGroupBucket(trafficTreatment_bucket2.build());

        // buckets:
        GroupBuckets all_group_buckets = new GroupBuckets(ImmutableList.of(bucket1, bucket2));

        // apply
        DefaultGroupDescription all_group = new DefaultGroupDescription(deviceId,
                GroupDescription.Type.ALL, all_group_buckets, key, select_group_id.id(), appId);

        groupService.addGroup(all_group);
        log.info("Add all group table to deviceiId<{}> groupId<{}>", deviceId.toString(), groupId);

    }


    /* sw2, all buckets do INT operation. */
    public void install_pof_all_group_rule_match_type_at_sw2(DeviceId deviceId, byte tableId, String int_type,String key_str, int groupId,
                                                      int priority, int usr_port, int collect_port, String mapInfo) {
        GroupId select_group_id = new GroupId(groupId);

        byte[] keyData = key_str.getBytes();
        final GroupKey key = new DefaultGroupKey(keyData);

        // bucket1: output
        TrafficTreatment.Builder trafficTreatment_bucket1 = DefaultTrafficTreatment.builder();
        List<OFAction> actions_bucket1 = new ArrayList<>();

        // add-int-field
        short int_field_id = -1;
        OFAction action_add_int_field = DefaultPofActions.addField(Protocol.INT_FIELD_ID, Protocol.INT_HEADER_DATA_OFF, Protocol.INT_HEADER_TYPE_LEN, mapInfo).action(); // 'mapInfo' should be 0xff
        OFAction action_add_func_field = DefaultPofActions.addField(Protocol.INT_DPID_ID, Protocol.INT_DATA_DPID_END_OFF, Protocol.INT_DATA_DPID_END_LEN, funcByteHexStr(deviceId)).action(); // for path revalidation

        // modify INT-ttl
        OFMatch20 Field_INT_ttl =  new OFMatch20();
        Field_INT_ttl.setFieldName("INT_ttl");
        Field_INT_ttl.setFieldId(Protocol.INT_TTL_ID);
        Field_INT_ttl.setOffset(Protocol.INT_HEADER_TTL_OFF);
        Field_INT_ttl.setLength(Protocol.INT_HEADER_TTL_LEN);
        OFAction action_inc_INT_ttl = DefaultPofActions.modifyField(Field_INT_ttl, 1).action();

        OFAction action_output1 = DefaultPofActions.output((short) 0, (short) 0, (short) 0, collect_port).action();
        actions_bucket1.add(action_add_int_field);    /* add int metadata. */

        actions_bucket1.add(action_inc_INT_ttl);      /* increment int_ttl field by 1 */
        actions_bucket1.add(action_output1);
        trafficTreatment_bucket1.add(DefaultPofInstructions.applyActions(actions_bucket1));

        // bucket1: weight
        GroupBucket bucket1 = DefaultGroupBucket.createAllGroupBucket(trafficTreatment_bucket1.build());

        // bucket2: action: add_int_field (auto-run-bucket1, then run bucket2) + output
        TrafficTreatment.Builder trafficTreatment_bucket2 = DefaultTrafficTreatment.builder();
        List<OFAction> actions_bucket2 = new ArrayList<>();
        OFAction action_output2 = DefaultPofActions.output((short) 0, (short) 0, (short) 0, usr_port).action();
//        actions_bucket2.add(action_add_int_field);    /* add int metadata. */
//        actions_bucket2.add(action_add_func_field);  /* This action used to revalidate path. */
//        actions_bucket2.add(action_inc_INT_ttl);      /* increment int_ttl field by 1 */
        actions_bucket2.add(action_output2);
        trafficTreatment_bucket2.add(DefaultPofInstructions.applyActions(actions_bucket2));

        // bucket2: weight
        GroupBucket bucket2 = DefaultGroupBucket.createAllGroupBucket(trafficTreatment_bucket2.build());

        // buckets:
        GroupBuckets all_group_buckets = new GroupBuckets(ImmutableList.of(bucket1, bucket2));

        // apply
        DefaultGroupDescription all_group = new DefaultGroupDescription(deviceId,
                GroupDescription.Type.ALL, all_group_buckets, key, select_group_id.id(), appId);

        groupService.addGroup(all_group);
        log.info("Add all group table at sw2");

    }


    public void remove_pof_group_tables(DeviceId deviceId, String key_str) {
        byte[] keyData = key_str.getBytes();
        final GroupKey key = new DefaultGroupKey(keyData);
        groupService.removeGroup(deviceId, key, appId);
        log.info("remove group table deviceId <>.", deviceId.toString());
    }


    public void install_pof_write_metadata_from_packet_entry(DeviceId deviceId, int tableId, int next_table_id,
                                                             String srcIP, int priority) {
        // match
        TrafficSelector.Builder trafficSelector = DefaultTrafficSelector.builder();
        ArrayList<Criterion> matchList = new ArrayList<>();
        matchList.add(Criteria.matchOffsetLength(Protocol.SIP_ID , Protocol.IPV4_SIP_OFF, Protocol.IPV4_SIP_LEN, srcIP, "ffffffff"));
        trafficSelector.add(Criteria.matchOffsetLength(matchList));

        // metadata bits
        short metadata_offset = 32;
        short udp_len_offset = 304;    // the offset of `len` field in udp
        short write_len = 16;          // the length of `len` field in udp

        // next_table_match_field (should same as next_table), here is still srcIP
        OFMatch20 next_table_match_srcIP = new OFMatch20();
        next_table_match_srcIP.setFieldId(Protocol.SIP_ID);
        next_table_match_srcIP.setFieldName("srcIP");
        next_table_match_srcIP.setOffset(Protocol.IPV4_SIP_OFF);
        next_table_match_srcIP.setLength(Protocol.IPV4_SIP_LEN);

        ArrayList<OFMatch20> match20List = new ArrayList<>();
        match20List.add(next_table_match_srcIP);

        byte next_table_match_field_num = 1;
        short next_table_packet_offset = 0;

        // instruction
        TrafficTreatment.Builder trafficTreatment = DefaultTrafficTreatment.builder();
        trafficTreatment.add(DefaultPofInstructions
                .writeMetadataFromPacket(metadata_offset, udp_len_offset, write_len));
        trafficTreatment.add(DefaultPofInstructions
                .gotoTable((byte) next_table_id, next_table_match_field_num, next_table_packet_offset, match20List));
//                .gotoDirectTable((byte) next_table_id, (byte) 0, (short) 0, 0, new OFMatch20()));

        long newFlowEntryId = flowTableStore.getNewFlowEntryId(deviceId, tableId);
        FlowRule.Builder flowRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .withSelector(trafficSelector.build())
                .withTreatment(trafficTreatment.build())
                .withPriority(priority)
                .withCookie(newFlowEntryId)
                .makePermanent();
        flowRuleService.applyFlowRules(flowRule.build());
    }


    public void removeGroupTables(DeviceId deviceId, String key_str) {
        byte[] keyData = key_str.getBytes();
        final GroupKey key = new DefaultGroupKey(keyData);
        groupService.removeGroup(deviceId, key, appId);
    }


    /**
     * util tools.
     */

    public String short2HexStr(short shortNum) {
        StringBuilder hex_str = new StringBuilder();
        byte[] b = new byte[2];
        b[1] = (byte) (shortNum & 0xff);
        b[0] = (byte) ((shortNum >> 8) & 0xff);

        return bytes_to_hex_str(b);
    }

    public String byte2HexStr(byte byteNum) {
        String hex = Integer.toHexString(   byteNum & 0xff);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }

    public String funcByteHexStr(DeviceId deviceId) {
        String device = deviceId.toString().substring(18, 20);   /* for 'pof:000000000000000x', get '0x' */
        byte dpid = Integer.valueOf(device).byteValue();
        int k = 2, b = 1;
        byte y = (byte) (k * dpid + b);   // simple linear function
        return byte2HexStr(y);
    }

    public String bytes_to_hex_str(byte[] b) {
        StringBuilder hex_str = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xff);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hex_str.append(hex);
        }
        return hex_str.toString();
    }

}
