SET: DEBUG, 1

DEFINE: local_addr_type, BYTE, 01
DEFINE: conn_addr, BYTE, F0 F1 F2 F3 F4 F5
DEFINE: local_addr, BYTE, F0 F1 F2 F3 F4 F5

IMPORT: hci_reset.sc

DEFINE: ext_adv_handle, BYTE, 01
DEFINE: local_addr_type, BYTE, 01
DEFINE: adv_peer_addr_type, BYTE, 00
DEFINE: adv_peer_addr, BYTE, 000000000000
DEFINE: prim_adv_phy, BYTE, 01
DEFINE: sec_adv_phy, BYTE, 01
DEFINE: sec_adv_max_skip, BYTE, 01
DEFINE: adv_sid, BYTE, 00
DEFINE: adv_scan_req_notify, BYTE, 00

#Set Adv Parameter
SEND: 01 3620 19 {ext_adv_handle} 01 00 20 00 00 20 00 00 07 {local_addr_type} {adv_peer_addr_type} {adv_peer_addr} 00 00 {prim_adv_phy} {sec_adv_max_skip} {sec_adv_phy} {adv_sid} {adv_scan_req_notify}
RECV: 04 0E 05 {n,1} 36 20 00 {tx_power,1}

#Set Adv Address
DEFINE: local_addr, BYTE, F5F5F5F5F5F5
SEND: 01 3520 07 {ext_adv_handle} {local_addr}
RECV: 04 0E 04 {n,1} 35 20 00

#Set Adv Data
DEFINE: adv_data_ext_len, BYTE, 5D
DEFINE: adv_data_ext, BYTE, 5C095F5F6578745F6164765F5F3031323334353637383930313233343536373839303132333435363738393031323334353637383930313233343536373839303132333435363738393031323334353637383930313233343536373839
SEND: 01 3720 61 {ext_adv_handle} 03 00 {adv_data_ext_len} {adv_data_ext}
RECV: 04 0E 04 {n,1} 37 20 00

#Enable Adv
DEFINE: ext_adv_dur, BYTE, 0000
DEFINE: ext_adv_max_evt, BYTE, 00
SEND: 01 3920 06 01 01 {ext_adv_handle} {ext_adv_dur} {ext_adv_max_evt}
RECV: 04 0E 04 {n,1} 39 20 00

DEBUG: Extend Advertise Started.

