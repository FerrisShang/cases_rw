SET: DEBUG, 1

DEFINE: local_addr_type, BYTE, 01
DEFINE: conn_addr, BYTE, F0 F1 F2 F3 F4 F5
DEFINE: local_addr, BYTE, F0 F1 F2 F3 F4 F5

IMPORT: hci_reset.sc
IMPORT: hci_start_adv_ext.sc
IMPORT: hci_connected.sc
#ignore adv set terminate
RECV: 04 3E 06 12 00 {adv_handle,1} {CON_HDL} {num_evt,1}

DEBUG: LE Set Connection CTE Transmit Parameters command
DEFINE: cte_types, BYTE 07
SEND: 01 5520 08 {CON_HDL} {cte_types} 0400010203
RECV: 04 0E 06 05 5520 00 {CON_HDL}

DEBUG: LE Connection CTE Response Enable command
SEND: 01 5720 03 {CON_HDL} 01
RECV: 04 0E 06 05 5720 00 {CON_HDL}

DELAY: 10000
IMPORT: hci_disconnect.sc
IMPORT: hci_disconnected.sc
