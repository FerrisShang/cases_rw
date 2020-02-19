#
IMPORT: hl_slave_cfg.sc

IMPORT: hci_reset.sc
IMPORT: hci_connect_as_slave.sc

#ignore adv set terminate
RECV: 04 3E 06 12 00 {adv_handle,1} {CON_HDL} {num_evt,1}

DEBUG:  LE Set Connection CTE Receive Parameters command
DEFINE: Sampling_Enable, BYTE, 01
DEFINE: Slot_Durations, BYTE, 01
DEFINE: Switching_Pattern_Length, BYTE, 4B
DEFINE: Antenna_IDs, BYTE, 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F 10 11 12 13 14 15 16 17 18 19 1A 1B 1C 1D 1E 1F 20 21 22 23 24 25 26 27 28 29 2A 2B 2C 2D 2E 2F 30 31 32 33 34 35 36 37 38 39 3A 3B 3C 3D 3E 3F 40 41 42 43 44 45 46 47 48 49 4A
SEND: 01 5420 50 {CON_HDL} {Sampling_Enable} {Slot_Durations} {Switching_Pattern_Length} {Antenna_IDs}
RECV: 04 0E 06 05 5420 00 {CON_HDL}

DEBUG:  LE Connection CTE Request Enable command
DEFINE: Enable, BYTE, 01
DEFINE: CTE_Request_Interval, BYTE, 04 00
DEFINE: Requested_CTE_Length, BYTE, 04
DEFINE: Requested_CTE_Type, BYTE, 01
SEND: 01 5620 07 {CON_HDL} {Enable} {CTE_Request_Interval} {Requested_CTE_Length} {Requested_CTE_Type}
RECV: 04 0E 06 05 5620 00 {CON_HDL}

SET: TIMEOUT, 10000
FOR: {i,1}, 0, 16
RECV: 04 3E 32 16 {CON_HDL} {PHY,1} {data_channel_index,1} {RSSI,2} {RSSI_ant_id,1} {CTE_Type,1} {slot_durations,1} {packet_status,1} {evt_counter,2} {sample_count,1} {IQ_sample,0}
LOOP

DEFINE: Disable, BYTE, 00
SEND: 01 5620 07 {CON_HDL} {Disable} {CTE_Request_Interval} {Requested_CTE_Length} {Requested_CTE_Type}
RECV: 04 0E 06 05 5620 00 {CON_HDL}
IGNORE: 04 3E 32 16 {CON_HDL} {_,0}
DELAY: 2000
IMPORT: hci_disconnect.sc
IMPORT: hci_disconnected.sc
