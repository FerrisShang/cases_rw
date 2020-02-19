
DEFINE: init_filter, BYTE, 00
DEFINE: init_phy, BYTE, 01
DEFINE: conn_scan_param, BYTE, A100 A100
DEFINE: conn_con_param, BYTE, 4000 8000 0000 0001
DEFINE: conn_ce_param, BYTE, 0000 8000

SEND: 01 4320 1A {init_filter} {LOC_ADDR_TYPE} {REM_ADDR_TYPE} {REM_ADDR} {init_phy} {conn_scan_param} {conn_con_param} {conn_ce_param}
RECV: 04 0F 04 00 {num,1} 43 20