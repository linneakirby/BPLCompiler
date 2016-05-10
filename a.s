.section .rodata
.WriteIntString: .string "%d "
.WritelnString: .string "\n"
.WriteStringString: .string "%s"
.s0: .string "in func"
.comm x, 8, 32
.comm y, 8, 32
.comm q, 8, 32
.text
.globl main
func:
movq %rsp, %rbx #move stack pointer to fp
subq $-8, %rsp #decrement stack pointer by -8 to make room for local vars
movq $.s0, %rsi #move .s0 into 2nd arg to prepare for printing
movq $.WriteStringString, %rdi #prepare to write a string
movl $0, %eax #reset ret
call printf
movq $5, %rax #move num to rax
push %rax
movq 16(%rbx), %rax #move i to rax
add 0(%rsp), %rax
add $8, %rsp #pop the stack
movq %rax, 16(%rbx) #move rax into position
movq 16(%rbx), %rax #move i to rax
addq -8, %rsp #restoring stack to original size
ret #return
addq $-8, %rsp #remove local vars
ret #return
test:
movq %rsp, %rbx #move stack pointer to fp
subq $-8, %rsp #decrement stack pointer by -8 to make room for local vars
movq 16(%rbx), %rax #move i to rax
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
movq $2, %rax #move num to rax
push %rax #push arg onto stack
push %rbx #push fp
call func #call func
pop %rbx #pop fp
addq $8, %rsp #remove args from stack
movq %rax, y #set value of y
movq y, %rax #move y to rax
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
addq $-8, %rsp #remove local vars
ret #return
main:
movq %rsp, %rbx #move stack pointer to fp
subq $-8, %rsp #decrement stack pointer by -8 to make room for local vars
movq $1, %rax #move num to rax
push %rax #push arg onto stack
push %rbx #push fp
call test #call test
pop %rbx #pop fp
addq $8, %rsp #remove args from stack
addq $-8, %rsp #remove local vars
ret #return
