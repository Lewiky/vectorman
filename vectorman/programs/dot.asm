# Load immediates
LOI r0 3
LOI r1 0
LOI r2 1
LOI r3 2
LOI r5 -3
LOI r6 0
# First loop - loading into memory
MUL r4 r0 r3
STR r1 r6 r1
ADD r1 r1 r2
SUB r4 r4 r2
BLE r5 r6 r4
# Setup for dot product
LOI r1 0
LOI r2 3
LOI r10 0
LOI r7 1
LOI r8 -7
# Second Loop - compute dot product
LOD r3 r1 r6
LOD r4 r2 r6
MUL r5 r3 r4
ADD r10 r10 r5
ADD r1  r1  r7
ADD r2  r2  r7
SUB r0  r0  r7
BLE r8 r6 r0
END