.section .rodata
.WriteIntString: .string "%d "
.WritelnString: .string "\n"
.WriteStringString: .string "%s"
.ReadIntString: .string "%d"
.text
.globl main
main:
movq %rsp, %rbx #move stack pointer to fp
subq $16, %rsp #decrement stack pointer by 16 to make room for local vars
movq $1, %rax #move num to rax
movq %rax, -8(%rbx) #move rax into position
movq %rbx, %rax #move x to rax
addq $-8, %rax
movq %rax, -16(%rbx) #move rax into position
movq -16(%rbx), %rax #move y to rax
movq 0(%rax), %rax #dereference pointer
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
addq $16, %rsp #remove local vars
ret #return
