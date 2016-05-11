.section .rodata
.WriteIntString: .string "%d "
.WritelnString: .string "\n"
.WriteStringString: .string "%s"
.s0: .string "i is now: "
.text
.globl main
whileTest:
movq %rsp, %rbx #move stack pointer to fp
subq $0, %rsp #decrement stack pointer by 0 to make room for local vars
movq $0, %rax #move num to rax
movq %rax, -8(%rbx) #move rax into position
movq -8(%rbx), %rax #move i to rax
push %rax
movq $10, %rax #move num to rax
cmp %eax, 0(%rsp) #relop compare
je L0
mov $1, %rax #condition is true
jmp L1
L0:
mov $0, %rax #condition is false
L1:
add $8, %rsp #pop the stack
L2:
cmp $0, %eax #compare result to 0
je L3 #jump to L3 if false
movq $1, %rax #move num to rax
push %rax
movq -8(%rbx), %rax #move i to rax
add 0(%rsp), %rax
add $8, %rsp #pop the stack
movq %rax, -8(%rbx) #move rax into position
movq $.s0, %rsi #move .s0 into 2nd arg to prepare for printing
movq $.WriteStringString, %rdi #prepare to write a string
movl $0, %eax #reset ret
call printf
movq -8(%rbx), %rax #move i to rax
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
jmp L2 #jump to L2 to continue while loop
L3:
movq -8(%rbx), %rax #move i to rax
addq $0, %rsp #restoring stack to original size
ret #return
addq $0, %rsp #remove local vars
ret #return
main:
movq %rsp, %rbx #move stack pointer to fp
subq $0, %rsp #decrement stack pointer by 0 to make room for local vars
push %rbx #push fp
call whileTest #call whileTest
pop %rbx #pop fp
addq $0, %rsp #remove args from stack
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
addq $0, %rsp #remove local vars
ret #return
