.section .rodata
.WriteStringString: .string "%s "
.WritelnString: .string "\n"
.S0: .string "Hi, Bob!"
.text
.globl main

main:
	movq %rsp, %rbx
	movq %rbx, %rax
	movq $.S0, %rax
	movq %rax, %rsi
	movq $.WriteStringString, %rdi
	movl $0, %eax
	call printf
