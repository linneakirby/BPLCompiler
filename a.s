.section .rodata
.WriteIntString: .string "%d "
.WritelnString: .string "\n"
.WriteStringString: .string "%s"
.text
.globl main
ifTest:
movq %rsp, %rbx #move stack pointer to fp
subq $0, %rsp #decrement stack pointer by 0 to make room for local vars
#ID: i
#DEPTH: 1
movq 16(%rbx), %rax #move i to rax
push %rax
movq $10, %rax #move num to rax
cmp %eax, 0(%rsp) #relop compare
jne L0
mov $1, %rax #condition is true
jmp L1
L0:
mov $0, %rax #condition is false
L1:
add $8, %rsp #pop the stack
cmp $0, %eax #compare result to 0
je L2 #jump to L2 if false
L2:
movq $0, %rax #move num to rax
addq $0, %rsp #restoring stack to original size
ret #return
addq $0, %rsp #remove local vars
ret #return
main:
movq %rsp, %rbx #move stack pointer to fp
subq $0, %rsp #decrement stack pointer by 0 to make room for local vars
movq $10, %rax #move num to rax
push %rax #push arg onto stack
push %rbx #push fp
call ifTest #call ifTest
pop %rbx #pop fp
addq $8, %rsp #remove args from stack
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
movq $4, %rax #move num to rax
push %rax #push arg onto stack
push %rbx #push fp
call ifTest #call ifTest
pop %rbx #pop fp
addq $8, %rsp #remove args from stack
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
addq $0, %rsp #remove local vars
ret #return
