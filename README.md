# Vectorman - Super Scalar Processor Simulator

This is a simulator for a superscalar processor built in Scala. It includes an assembler that coverts a propietary instruction set to machine code to be executed.

## Building and Running

Run the `build.sh` script inside the `vectorman` directory to compile and start sbt shell

Once inside the sbt shell, use `run programs/{ASM_FILE}.asm` to execute a program.

## Instruction Set

| Binary | Instruction | arg0 | arg1 | arg2 | notes                    |
|--------|-------------|------|------|------|--------------------------|
| 0000   | add         | r0   | r1   | r2   |  r0 = r1 + r2            |
| 0001   | sub         | r0   | r1   | r2   |  r0 = r1 - r2            |
| 0010   | mul         | r0   | r1   | r2   |  r0 = r1 * r2            |
| 0011   | div         | r0   | r1   | r2   |  r0 = r1 / r2            |
| 0100   | lod         | r0   | r1   | r2   |  r0 = load(r1 + r2)      |
| 0101   | str         | r0   | r1   | r2   |  mem(r1 + r2) = r0       |
| 0110   | bra         | r0   |      |      |  pc = r0                 |
| 0111   | jmp         | r0   |      |      |  pc = pc + r0            |
| 1000   | ble         | r0   | r1   | r2   |  pc = pc + r0 if r1 <= r2|
| 1001   | cmp         | r0   | r1   | r2   |  r0 = compare(r1, r2)    |
| 1010   | and         | r0   | r1   | r2   |  r0 = r1 && r2           |
| 1011   | not         | r0   | r1   |      |  r0 = !r1                |
| 1100   | rsh         | r0   | r1   | r2   |  r0 = r1 >> r2           |
| 1101   | beq         | r0   | r1   | r2   |  pc = pc + r0 if r1 == r2|
| 1110   | cpy         | r0   | r1   |      |  r0 = r1                 |
| 1111   | nop         |      |      |      |  nop                     |
