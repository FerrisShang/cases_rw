# Connect as slave

IMPORT: hci_start_adv_ext.sc
IMPORT: hci_connected.sc

#ignore adv set terminate
RECV: 04 3E 06 12 00 {EXT_ADV_FOR_CONN_HANDLE,1} {CON_HDL} {num_evt,1}