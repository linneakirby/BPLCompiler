.section .rodata
.WriteIntString: .string "%d "
.WritelnString: .string "\n"
.WriteStringString: .string "%s"
.text
.globl main
main:
movq $5, %rax
push %rax
movq $5, %rax
imul 0(%rsp), %rax
add $8, %rsp #pop the stack
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $5, %rax
push %rax
movq $5, %rax
push %rbx
movq 0(%rsp), %rbx
cltq
cqto
idiv %ebx #divide
pop %rbx
add $8, %rsp #pop the stack
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $5, %rax
push %rax
movq $5, %rax
push %rbx
movq 0(%rsp), %rbx
cltq
cqto
idiv %ebx #divide
movq %rdx, %rax #get result of mod
pop %rbx
add $8, %rsp #pop the stack
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $5, %rax
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
