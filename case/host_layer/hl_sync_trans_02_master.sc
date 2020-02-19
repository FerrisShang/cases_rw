#sync trans master
IMPORT: hl_master_cfg.sc

IMPORT: rw_reset.sc
IMPORT: rw_connect_as_master.sc

#Create activity
SEND: 05a00d0d0010000200 A300
RECV: 05A40D10000D000300 {idx_sync, 1} 0300
RECV: 05000D10000D000200 A300
#Start activity (Enable sync trans)
WARNING: Enable sync trans
DEFINE: SYNC_SKIP, BYTE, 0000
DEFINE: SYNC_TOUT, BYTE, 0040
DEFINE: SYNC_TYPE, BYTE, 02
DEFINE: SYNC_CONIDX, BYTE, 00
DEFINE: SYNC_REPORT_DIS, BYTE, 00
DEFINE: SYNC_CTE_TYPE, BYTE, 00
#Start
SEND: 05a10d0d0010003a00 A4 {idx_sync} {SYNC_SKIP} {SYNC_TOUT} {SYNC_TYPE} {SYNC_CONIDX} 000000000000 00 {SYNC_REPORT_DIS} {SYNC_CTE_TYPE} 0000000000000000000000000000000000000000000000000000000000000000000000000000000000
RECV: 05000D10000D000200 A400
SET: TIMEOUT, 5000
RECV: 05A90D10000D00 1000 {actv_idx,1} {phy,1} {intv,2} {adv_sid,1} {clk_acc,1} {addr,6} {addr_type,1} 00 {serv_data,2}
WARNING: SYNC Established received {actv_idx} {adv_sid} {addr} {addr_type} {serv_data}

SET: TIMEOUT, 2000
FOR: {i,1},0,10
RECV: 05A70D10000D00 {L,2} {idx,1} {info,1} {trans_addr,7} {target_addr,7} {tx_pwr,1} {rssi,1} {phy_prim,1} {phy_sec,1} {adv_sid,2} {adv_intv,2} {len,2} {adv_data,0}
WARNING: ADV_REPORT received {adv_sid} {adv_intv} {adv_data}
LOOP

WARNING: Stop Periodic Advertising Report
SEND: 05af0d0d0010000300 B0 {idx_sync} 00
RECV: 05000D10000D000200 B000

DELAY: 2000

WARNING: Re-start Periodic Advertising Report
SEND: 05af0d0d0010000300 B0 {idx_sync} 01
RECV: 05000D10000D000200 B000

FOR: {i,1},0,10
RECV: 05A70D10000D00 {L,2} {idx,1} {info,1} {trans_addr,7} {target_addr,7} {tx_pwr,1} {rssi,1} {phy_prim,1} {phy_sec,1} {adv_sid,2} {adv_intv,2} {len,2} {adv_data,0}
WARNING: ADV_REPORT received {adv_sid} {adv_intv} {adv_data}
LOOP

WARNING: Stop sync trans
SEND: 05a20d0d0010000200 A5 {idx_sync}
RECV: 05 A5 0D 10 00 0D 00 04 00 {idx,1} 03 00 {per_stop,1}
RECV: 05 00 0D 10 00 0D 00 02 00 A5 00

IMPORT: rw_disconnected.sc
IMPORT: hl_finish.sc
