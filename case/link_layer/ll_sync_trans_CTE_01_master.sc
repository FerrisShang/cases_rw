SET: DEBUG, 1

DEFINE: local_addr_type, BYTE, 01
DEFINE: conn_addr, BYTE, F5 F5 F5 F5 F5 F5
DEFINE: local_addr, BYTE, F0 F1 F2 F3 F4 F5

IMPORT: hci_reset.sc
IMPORT: hci_connect.sc
IMPORT: hci_connected.sc
#Set Periodic Adv Sync Trans Param
DEFINE: sync_trans_mode, BYTE, 02
DEFINE: sync_trans_skip, BYTE, 0000
DEFINE: sync_trans_tout, BYTE, 0010
DEFINE: sync_trans_cte, BYTE, 00
SEND: 01 5C 20 08 {CON_HDL} {sync_trans_mode} {sync_trans_skip} {sync_trans_tout} {sync_trans_cte}
RECV: 04 0E 06 {num,1} 5C 20 00 {CON_HDL}

#TEMP ignore
IGNORE: 043E{l,1}07{_,0}

RECV: 04 3E 14 18 00 {CON_HDL} {serv_data,2} {sync_handle,2} {sid,1} {addr_type,1} {addr,6} {phy,1} {interval,2} {acc,1}
DEBUG: Periodic Adv sync: {serv_data} {sync_handle} {sid} {addr_type} {addr} {phy} {interval} {acc}

IMPORT: hci_start_connless_iq_Sampling.sc

SET: TIMEOUT, 5000

FOR: {i,1}, 0, 10
RECV: {_,0}
LOOP
#terminate sync
SEND: 01 46 20 02 {sync_handle}
RECV: 04 0E 04 {num,1} 46 20 00
IGNORE: 04 3E {l,1} 15 {_,0}
IGNORE: 04 3E {l,1} 0F {_,0}

# Wait for disconnect
IMPORT: hci_disconnected.sc