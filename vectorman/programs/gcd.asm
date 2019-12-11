LOI r10 1
LOI r11 0
#(#1)
LOI r12 6
#(#2)
LOI r13 3
#(#3)
LOI r14 -3
#(#4)
LOI r15 -5
# a and b > 0
LOI r0 12
LOI r1 34
#(#1)
BEQ r12 r0 r1
#(#2)
BLE r13 r0 r1
SUB r0 r0 r1
#(#3)
JMP r14
SUB r1 r1 r0
#(#4)
JMP r15
ADD r10 r10 r11
END
