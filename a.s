.section .rodata
.WriteIntString: .string "%d "
.WritelnString: .string "\n"
.WriteStringString: .string "%s"
.s0: .string "i is now: "
.text
.globl main
whileTest:
movq %rsp, %rbx #move stack pointer to fp
subq $16, %rsp #decrement stack pointer by 16 to make room for local vars
movq $0, %rax #move num to rax
movq %rax, -8(%rbx) #move rax into position
L0:
movq -8(%rbx), %rax #move i to rax
push %rax
movq $5, %rax #move num to rax
cmp %eax, 0(%rsp) #relop compare
je L2
mov $1, %rax #condition is true
jmp L3
L2:
mov $0, %rax #condition is false
L3:
add $8, %rsp #pop the stack
cmp $0, %eax #compare result to 0
je L1 #jump to L1 if false
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
movq $0, %rax #move num to rax
movq %rax, -16(%rbx) #move rax into position
L4:
movq -16(%rbx), %rax #move l to rax
push %rax
movq $3, %rax #move num to rax
cmp %eax, 0(%rsp) #relop compare
jge L6
mov $1, %rax #condition is true
jmp L7
L6:
mov $0, %rax #condition is false
L7:
add $8, %rsp #pop the stack
cmp $0, %eax #compare result to 0
je L5 #jump to L5 if false
movq $1, %rax #move num to rax
push %rax
movq -16(%rbx), %rax #move l to rax
add 0(%rsp), %rax
add $8, %rsp #pop the stack
movq %rax, -16(%rbx) #move rax into position
movq -16(%rbx), %rax #move l to rax
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
jmp L4 #jump to L4 to continue while loop
L5:
jmp L0 #jump to L0 to continue while loop
L1:
movq -8(%rbx), %rax #move i to rax
addq $16, %rsp #restoring stack to original size
ret #return
addq $16, %rsp #remove local vars
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
