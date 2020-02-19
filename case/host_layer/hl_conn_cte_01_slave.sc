#sync trans slave
IMPORT: hl_slave_cfg.sc

IMPORT: rw_reset.sc
IMPORT: rw_start_adv_period.sc
IMPORT: rw_connect_as_slave.sc

DEFINE: Sampling_Enable, BYTE, 01
DEFINE: Slot_Durations, BYTE, 01
DEFINE: Switching_Pattern_Length, BYTE, 4B
DEFINE: Antenna_IDs, BYTE, 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F 10 11 12 13 14 15 16 17 18 19 1A 1B 1C 1D 1E 1F 20 21 22 23 24 25 26 27 28 29 2A 2B 2C 2D 2E 2F 30 31 32 33 34 35 36 37 38 39 3A 3B 3C 3D 3E 3F 40 41 42 43 44 45 46 47 48 49 4A
WARNING: LE Set Connection CTE Receive Parameters command
SEND: 05 390E0E0010004F00 21 {Sampling_Enable} {Slot_Durations} {Switching_Pattern_Length} {Antenna_IDs}
RECV: 05 000E10000E000200 21 00

WARNING:  LE Connection CTE Request Enable command
DEFINE: Enable, BYTE, 01
DEFINE: CTE_Request_Interval, BYTE, 04 00
DEFINE: Requested_CTE_Length, BYTE, 04
DEFINE: Requested_CTE_Type, BYTE, 01
SEND: 05 3A0E0E0010000600 22 {Enable} {CTE_Request_Interval} {Requested_CTE_Length} {Requested_CTE_Type}
RECV: 05 000E10000E000200 22 00

FOR: {i,1}, 0, 16
RECV: 05 3C0E10000E00{len,2} {rx_phy,1} {data_channel_idx,1} {rssi,2} {rssi_antenna_id,1} {cte_type,1} {slot_dur,1} {pkt_stat,1} {con_evt_cnt,2} {nb_samples,1} {sample,0}
WARNING: Report: {rx_phy} {data_channel_idx} {rssi} {rssi_antenna_id} {cte_type} {slot_dur} {pkt_stat} {con_evt_cnt}
DEBUG: :       {sample}
LOOP

DEFINE: Disable, BYTE, 00
SEND: 05 3A0E0E0010000600 22 {Disable} {CTE_Request_Interval} {Requested_CTE_Length} {Requested_CTE_Type}
RECV: 05 000E10000E000200 22 00
DELAY: 2000
IMPORT: rw_disconnect.sc
IMPORT: rw_disconnected.sc
#Disconnected complete
RECV: 05 000E10000E000200 0100

IMPORT: hl_finish.sc
