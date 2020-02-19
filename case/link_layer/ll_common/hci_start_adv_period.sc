
#Set Ext Adv Parameter
DEFINE: period_ext_adv_prop, BYTE, 4000
DEFINE: period_ext_adv_intv, BYTE, 200000 200000
DEFINE: local_addr_type, BYTE, 01
DEFINE: adv_peer_addr_type, BYTE, 00
DEFINE: adv_peer_addr, BYTE, 000000000000
DEFINE: prim_adv_phy, BYTE, 01
DEFINE: sec_adv_phy, BYTE, 01
DEFINE: sec_adv_max_skip, BYTE, 00
DEFINE: period_adv_sid, BYTE, 03
DEFINE: adv_scan_req_notify, BYTE, 00
SEND: 01 3620 19 {EXT_PER_ADV_HANDLE} {period_ext_adv_prop} {period_ext_adv_intv} 07 {local_addr_type} {adv_peer_addr_type} {adv_peer_addr} 00 00 {prim_adv_phy} {sec_adv_max_skip} {sec_adv_phy} {period_adv_sid} {adv_scan_req_notify}
RECV: 04 0E 05 {n,1} 36 20 00 {tx_power,1}
DEBUG: Periodic Ext Param: {period_ext_adv_intv} {sec_adv_max_skip} {period_adv_sid}

#Set Periodic Advertising Parameters
DEFINE: period_adv_intv, BYTE, 6000 8000
DEFINE: perioid_adv_prop, BYTE, 4000
SEND: 01 3E 20 07 {EXT_PER_ADV_HANDLE} {period_adv_intv} {perioid_adv_prop}
RECV: 04 0E 04 {n,1} 3E 20 00
DEBUG: Periodic peroid Param: {period_adv_intv} {perioid_adv_prop}

#Set Adv Address
DEFINE: local_addr, BYTE, F5F5F5F5F5F5
SEND: 01 3520 07 {EXT_PER_ADV_HANDLE} {local_addr}
RECV: 04 0E 04 05 35 20 00

#Set Extend Advertising Data
DEFINE: period_ext_adv_data_len, BYTE, 0D
DEFINE: period_ext_adv_data, BYTE, 0C095F5F6578745F6164765F5F
SEND: 01 3720 11 {EXT_PER_ADV_HANDLE} 03 00 {period_ext_adv_data_len} {period_ext_adv_data}
RECV: 04 0E 04 {n,1} 37 20 00

#Set Periodic Advertising Data
DEFINE: period_adv_data_len, BYTE, 13
DEFINE: period_adv_data, BYTE, 5F5F706572696F645F6164765F646174615F5F
SEND: 01 3F 20 16 {EXT_PER_ADV_HANDLE} 03 {period_adv_data_len} {period_adv_data}
RECV: 04 0E 04 {n,1} 3F 20 00

#Set Ext Advertising Enable
DEFINE: period_adv_dur, BYTE, 0000
DEFINE: period_adv_max_evt, BYTE, 00
SEND: 01 3920 06 01 01 {EXT_PER_ADV_HANDLE} {period_adv_dur} {period_adv_max_evt}
RECV: 04 0E 04 {n,1} 39 20 00

#Set Periodic Advertising Enable
SEND: 01 40 20 02 01 {EXT_PER_ADV_HANDLE}
RECV: 04 0E 04 {n,1} 40 20 00

DEBUG: Periodic Advertise Started.