DEFINE: CTE_LEN, BYTE, 02
DEFINE: CTE_TYPE, BYTE, 00
DEFINE: CTE_COUNT, BYTE, 04
DEFINE: CTE_SP_LEN, BYTE, 30
DEFINE: CTE_ANT_IDS, BYTE, 000102030405060708090A0B0C0D0E0F 000102030405060708090A0B0C0D0E0F 000102030405060708090A0B0C0D0E0F
SEND: 01 5120 35 {period_adv_handle} {CTE_LEN} {CTE_TYPE} {CTE_COUNT} {CTE_SP_LEN} {CTE_ANT_IDS}
RECV: 04 0E 04 05 51 20 00
SEND: 01 5220 02 {period_adv_handle} 01
RECV: 04 0E 04 05 52 20 00