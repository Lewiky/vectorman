#Load immediates for sorting
# r0 base address for array
# r1 index for array access
# r10 stores 1
# r11 stores 0
# r12 #1
# r13 #2 
LOI r0 0
LOI r1 0
LOI r10 1
LOI r11 0
LOI r12 4
LOI r13 -8
LOI r14 -12
# length = 10
LOI r2 30
	# changed = False
	LOI r3 0
	# length --
	SUB r2 r2 r10
	#Set i to 0
	LOI r1 0
		# inner loop
		# item at index
		LOD r4 r0 r1
		# index + 1
		ADD r5 r1 r10
		# item at index + 1
		LOD r6 r0 r5
		# if item at index > item at index +1 (#1)
		BLE r12 r4 r6
		# Swap item at index item at index + 1
		# Store index + 1 in index
		STR r6 r0 r1
		# Store index in index + 1
		STR r4 r0 r5
		# Has changed = true
		LOI r3 1
	#increment index 
	ADD r1 r1 r10
	#for i in 0 until length (#2)
	BLE r13 r1 r2
# Until has changed is false
BEQ r14 r3 r10
END
