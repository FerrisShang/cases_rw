CUSTOM: DUMP_RAW
IMPORT: ll_master_cfg.sc

IMPORT: hci_reset.sc

FOR: {i,1}, 0, 32
	IMPORT: hci_connect_as_master.sc

	WARNING: Delay 100ms then disconnect
	DELAY: 100
	CUSTOM: SYNC

	# Wait for disconnect
	IMPORT: hci_disconnected.sc
LOOP