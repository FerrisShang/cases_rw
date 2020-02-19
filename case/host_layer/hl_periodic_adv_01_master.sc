#sync trans master
IMPORT: hl_master_cfg.sc

IMPORT: rw_reset.sc

IMPORT: rw_start_scan_ext_adv.sc



FOR: {cnt,1},0,64
	SET: TIMEOUT, 5000
	#Create activity
	SEND: 05a00d0d0010000200 A3{LOC_ADDR_TYPE}
	RECV: 05A40D10000D000300 {idx_sync, 1} 0300
	RECV: 05000D10000D000200 A300
	IGNORE: 05A70D10000D00{_,0}

	#Start activity periodic Adv
	WARNING: Start sync periodic Adv
	DEFINE: SYNC_SKIP, BYTE, 0000
	DEFINE: SYNC_TOUT, BYTE, 0010
	DEFINE: SYNC_TYPE, BYTE, 00
	DEFINE: SYNC_REPORT_DIS, BYTE, 00
	DEFINE: SYNC_CTE_TYPE, BYTE, 00
	#Start
	DEFINE:EXT_PER_ADV_SID,BYTE,00
	SEND: 05A10D0D0010003A00 A4{idx_sync} {SYNC_SKIP}{SYNC_TOUT}{SYNC_TYPE}{CON_IDX}{REM_ADDR}{REM_ADDR_TYPE}{EXT_PER_ADV_SID}{SYNC_REPORT_DIS}{SYNC_CTE_TYPE}00000000000000000000000000000000000000000000000000000000000000000000000000000000
	RECV: 05000D10000D000200 A400
	IGNORE: 05A70D10000D00{_,0}
	SET: TIMEOUT, 2000
	
	RECV: 05A90D10000D00 1000 {actv_idx,1} {phy,1} {intv,2} {adv_sid,1} {clk_acc,1} {addr,6} {addr_type,1} 00 {serv_data,2}
	IGNORE: 05A70D10000D00{_,0}
	WARNING: SYNC Established received {actv_idx} {adv_sid} {addr} {addr_type} {serv_data}

	SET: TIMEOUT, 2000
	FOR: {i,1},0,5
		RECV: 05A70D10000D00 {L,2} {idx,1} {info,1} 00000000000000 {target_addr,7} {tx_pwr,1} {rssi,1} {phy_prim,1} {phy_sec,1} {EXT_PER_ADV_SID} {adv_intv,2} {len,2} {adv_data,0}
		IGNORE: 05A70D10000D00{_,0}
		WARNING: ADV_REPORT received {info} {adv_sid} {adv_intv} {adv_data}
	LOOP

	SEND: 05a20d0d0010000200 A5 {idx_sync}
	RECV: 05 A5 0D 10 00 0D 00 04 00 {idx,1} 03 00 {per_stop,1}
	RECV: 05 00 0D 10 00 0D 00 02 00 A5 00
	IGNORE: 05A70D10000D00 {_,0}
	WARNING: Stop sync trans
	#Delete sync periodic activity
	SEND: 05a30d0d0010000200 A7 {idx_sync}
	RECV: 05 00 0D 10 00 0D 00 02 00 A7 00
	IGNORE: 05A70D10000D00 {_,0}
	WARNING: Periodic sync activity deleted
	DELAY: 2000
	CUSTOM: FLUSH
LOOP

CUSTOM: SYNC
IMPORT: hl_finish.sc

