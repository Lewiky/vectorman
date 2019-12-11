LOI r10 1
LOI r11 0
LOI r12 7
LOI r13 -6
#a and b
LOI r0 2333333434
LOI r1 3342494242
#loop
BEQ r12 r11 r1
#calculate a mod b
DIV r2 r0 r1
MUL r2 r2 r1
SUB r2 r0 r2
# a = b
CPY r0 r1
# b = a mod b
CPY r1 r2
JMP r13
ADD r10 r10 r11
END
