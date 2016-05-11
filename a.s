.section .rodata
.WriteIntString: .string "%d "
.WritelnString: .string "\n"
.WriteStringString: .string "%s"
.s0: .string "string test success!"
.s1: .string "string test"
.s2: .string "test"
.text
.globl main
stringTest:
movq %rsp, %rbx #move stack pointer to fp
subq $0, %rsp #decrement stack pointer by 0 to make room for local vars
movq $.s0, %rax #move string literal to rax
addq $0, %rsp #restoring stack to original size
ret #return
addq $0, %rsp #remove local vars
ret #return
main:
movq %rsp, %rbx #move stack pointer to fp
subq $8, %rsp #decrement stack pointer by 8 to make room for local vars
movq $.s1, %rax #move string literal to rax
movq %rax, -8(%rbx) #move rax into position
movq -8(%rbx), %rax #move s to rax
movq %rax, %rsi #move rax into 2nd arg to prepare for printing
movq $.WriteStringString, %rdi #prepare to write a string
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
movq $.s2, %rax #move string literal to rax
movq %rax, %rsi #move rax into 2nd arg to prepare for printing
movq $.WriteStringString, %rdi #prepare to write a string
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
push %rbx #push fp
call stringTest #call stringTest
pop %rbx #pop fp
addq $0, %rsp #remove args from stack
movq %rax, %rsi #move rax into 2nd arg to prepare for printing
movq $.WriteStringString, %rdi #prepare to write a string
movl $0, %eax #reset ret
call printf
addq $8, %rsp #remove local vars
ret #return
