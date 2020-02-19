# Reset device

# Reset device 1st
SEND: 01 03 0c 00

# Wait for reset to reflush the receive buffer, remove junk message such as reports.
DELAY: 50
CUSTOM: FLUSH

# Reset device 2nd
SEND: 01 03 0c 00
RECV: 04 0E 04 05 03 0C 00

SEND: 01 01 20 08 FF FF FF 07 00 00 00 00
RECV: 04 0E 04 05 01 20 00

# Set Random Address
SEND: 01 0520 06 {LOC_ADDR}
RECV: 04 0E 04 05 05 20 00

DELAY: 50
WARNING: Reset device
