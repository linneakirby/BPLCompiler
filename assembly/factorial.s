.section .rodata
.WriteIntString: .string "%d "
.WritelnString: .string "\n"
.text
.globl main

fact:
	movq %rsp, %rbx #set up frame pointer
	movq 16(%rbx), %rax #get first arg
	cmpq $1, %rax #compare 1 to first arg 
	jg .L0 #if 1 <= to first arg
	movq $1, %rax #return 1
	ret
.L0:
	add $-1, %rax #decrement first arg
	push %rax #push first arg to stack
	push %rbx #push frame pointer
	call fact
	pop %rbx #restore fp
	add $8, %rsp #remove arg from stack
	imul 16(%rbx), %rax #multiply return value by yourself
	ret
main:
	movq %rsp, %rbx #set up frame pointer
	push $1 #push arg onto stack
	push %rbx #push fp onto stack
	call fact 
	pop %rbx #restore fp
	add $8, %rsp #remove arg from stack
	movq %rax, %rsi #move return value to second arg 
	movq $0, %rax #reset ret
	movq $.WriteIntString, %rdi #move WriteIntString to first arg
	call printf
