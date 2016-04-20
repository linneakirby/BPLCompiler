.section .rodata
.WriteStringString: .string "%s "
.WriteIntString: .string "%d "
.WritelnString: .string "\n"
.text
.globl main

main:
	movl $0, %eax
.L0:
	cmpl $10, %eax
	jge .L1
	movq %eax, %rsi
	movq $.WriteIntString, %rdi
	call printf
.L1
