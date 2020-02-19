#
IMPORT: ll_master_cfg.sc

IMPORT: hci_reset.sc
IMPORT: hci_connect_as_master.sc

FOR: {loop,1}, 0, 20
	#Set Periodic Adv Sync Trans Param
	DEFINE: sync_trans_mode, BYTE, 02
	DEFINE: sync_trans_skip, BYTE, 0000
	DEFINE: sync_trans_tout, BYTE, 0010
	DEFINE: sync_trans_cte, BYTE, 00
	SEND: 01 5C 20 08 {CON_HDL} {sync_trans_mode} {sync_trans_skip} {sync_trans_tout} {sync_trans_cte}
	RECV: 04 0E 06 {num,1} 5C 20 00 {CON_HDL}
	CUSTOM: SYNC

	SET: TIMEOUT, 10000
	RECV: 04 3E 14 18 00 {CON_HDL} {serv_data,2} {sync_handle,2} {sid,1} {addr_type,1} {addr,6} {phy,1} {interval,2} {acc,1}
	WARNING: Periodic Adc sync success: {serv_data} {sync_handle} {sid} {addr_type} {addr} {phy} {interval} {acc}
	FOR: {i,1}, 0, 10
		RECV: 04 3E {l,1} 0F {per_adv_data,0}
		WARNING: Periodic Adv report received.
	LOOP

	#terminate sync
	WARNING: Terminate Periodic Adv.
	SEND: 01 46 20 02 {sync_handle}
	RECV: 04 0E 04 {num,1} 46 20 00
	IGNORE: 04 3E {l,1} 0F {per_adv_data,0}
LOOP

CUSTOM: SYNC

# Wait for disconnect
IMPORT: hci_disconnected.sc