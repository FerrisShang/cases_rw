#
IMPORT: hl_master_cfg.sc

IMPORT: hci_reset.sc
IMPORT: hci_connect_as_master.sc

DEBUG: LE Set Connection CTE Transmit Parameters command
DEFINE: cte_types, BYTE 07
SEND: 01 5520 08 {CON_HDL} {cte_types} 0400010203
RECV: 04 0E 06 05 5520 00 {CON_HDL}

DEBUG: LE Connection CTE Response Enable command
SEND: 01 5720 03 {CON_HDL} 01
RECV: 04 0E 06 05 5720 00 {CON_HDL}

DELAY: 10000
# Wait for disconnect
IMPORT: hci_disconnected.sc