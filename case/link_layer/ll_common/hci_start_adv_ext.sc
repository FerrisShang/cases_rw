#
DEFINE: adv_peer_addr_type, BYTE, 00
DEFINE: adv_peer_addr, BYTE, 000000000000
DEFINE: prim_adv_phy, BYTE, 01
DEFINE: sec_adv_phy, BYTE, 01
DEFINE: sec_adv_max_skip, BYTE, 00
DEFINE: adv_sid, BYTE, 00
DEFINE: adv_scan_req_notify, BYTE, 00

#Set Adv Parameter
SEND: 01 3620 19 {EXT_ADV_FOR_CONN_HANDLE} 01 00 20 00 00 20 00 00 07 {LOC_ADDR_TYPE} {adv_peer_addr_type} {adv_peer_addr} 00 00 {prim_adv_phy} {sec_adv_max_skip} {sec_adv_phy} {adv_sid} {adv_scan_req_notify}
RECV: 04 0E 05 {n,1} 36 20 00 {tx_power,1}

#Set Adv Address
SEND: 01 3520 07 {EXT_ADV_FOR_CONN_HANDLE} {LOC_ADDR}
RECV: 04 0E 04 {n,1} 35 20 00

#Set Adv Data
DEFINE: adv_data_ext_len, BYTE, 0D
DEFINE: adv_data_ext, BYTE, 0C095F5F6578745F6164765F5F
SEND: 01 3720 11 {EXT_ADV_FOR_CONN_HANDLE} 03 00 {adv_data_ext_len} {adv_data_ext}
RECV: 04 0E 04 {n,1} 37 20 00

#Enable Adv
DEFINE: ext_adv_dur, BYTE, 0000
DEFINE: ext_adv_max_evt, BYTE, 00
SEND: 01 3920 06 01 01 {EXT_ADV_FOR_CONN_HANDLE} {ext_adv_dur} {ext_adv_max_evt}
RECV: 04 0E 04 {n,1} 39 20 00

DEBUG: Extend Advertise Started.