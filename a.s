.section .rodata
.WriteIntString: .string "%d "
.WritelnString: .string "\n"
.WriteStringString: .string "%s"
.ReadIntString: .string "%d"
.s0: .string "The first "
.s1: .string "primes are "
.text
.globl main
isPrime:
movq %rsp, %rbx #move stack pointer to fp
subq $8, %rsp #decrement stack pointer by 8 to make room for local vars
movq 16(%rbx), %rax #move n to rax
push %rax
movq $2, %rax #move num to rax
cmp %eax, 0(%rsp) #relop compare
jge L0
mov $1, %rax #condition is true
jmp L1
L0:
mov $0, %rax #condition is false
L1:
add $8, %rsp #pop the stack
cmp $0, %eax #compare result to 0
je L2 #jump to L2 if false
movq $0, %rax #move num to rax
addq $8, %rsp #restoring stack to original size
ret #return
L2:
movq 16(%rbx), %rax #move n to rax
push %rax
movq $4, %rax #move num to rax
cmp %eax, 0(%rsp) #relop compare
jge L3
mov $1, %rax #condition is true
jmp L4
L3:
mov $0, %rax #condition is false
L4:
add $8, %rsp #pop the stack
cmp $0, %eax #compare result to 0
je L5 #jump to L5 if false
movq $1, %rax #move num to rax
addq $8, %rsp #restoring stack to original size
ret #return
L5:
movq $2, %rax #move num to rax
movq %rax, -8(%rbx) #move rax into position
L6:
movq -8(%rbx), %rax #move divisor to rax
push %rax
movq 16(%rbx), %rax #move n to rax
cmp %eax, 0(%rsp) #relop compare
jge L8
mov $1, %rax #condition is true
jmp L9
L8:
mov $0, %rax #condition is false
L9:
add $8, %rsp #pop the stack
cmp $0, %eax #compare result to 0
je L7 #jump to L7 if false
movq -8(%rbx), %rax #move divisor to rax
push %rax
movq 16(%rbx), %rax #move n to rax
movq 0(%rsp), %rbp
cltq
cqto
idivl %ebp #divide
movq %rdx, %rax #get result of mod
add $8, %rsp #pop the stack
push %rax
movq $0, %rax #move num to rax
cmp %eax, 0(%rsp) #relop compare
jne L10
mov $1, %rax #condition is true
jmp L11
L10:
mov $0, %rax #condition is false
L11:
add $8, %rsp #pop the stack
cmp $0, %eax #compare result to 0
je L12 #jump to L12 if false
movq $0, %rax #move num to rax
addq $8, %rsp #restoring stack to original size
ret #return
L12:
movq $1, %rax #move num to rax
push %rax
movq -8(%rbx), %rax #move divisor to rax
add 0(%rsp), %rax
add $8, %rsp #pop the stack
movq %rax, -8(%rbx) #move rax into position
jmp L6 #jump to L6 to continue while loop
L7:
movq $1, %rax #move num to rax
addq $8, %rsp #restoring stack to original size
ret #return
addq $8, %rsp #remove local vars
ret #return
main:
movq %rsp, %rbx #move stack pointer to fp
subq $24, %rsp #decrement stack pointer by 24 to make room for local vars
movq $0, %rax #move num to rax
movq %rax, -8(%rbx) #move rax into position
movq $0, %rax #move num to rax
movq %rax, -24(%rbx) #move rax into position
movq $10, %rax #move num to rax
movq %rax, -16(%rbx) #move rax into position
movq $.s0, %rax #move string literal to rax
movq %rax, %rsi #move rax into 2nd arg to prepare for printing
movq $.WriteStringString, %rdi #prepare to write a string
movl $0, %eax #reset ret
call printf
movq -16(%rbx), %rax #move max to rax
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $.s1, %rax #move string literal to rax
movq %rax, %rsi #move rax into 2nd arg to prepare for printing
movq $.WriteStringString, %rdi #prepare to write a string
movl $0, %eax #reset ret
call printf
L13:
movq -24(%rbx), %rax #move cur to rax
push %rax
movq -16(%rbx), %rax #move max to rax
cmp %eax, 0(%rsp) #relop compare
jge L15
mov $1, %rax #condition is true
jmp L16
L15:
mov $0, %rax #condition is false
L16:
add $8, %rsp #pop the stack
cmp $0, %eax #compare result to 0
je L14 #jump to L14 if false
movq -8(%rbx), %rax #move n to rax
push %rax #push arg onto stack
push %rbx #push fp
call isPrime #call isPrime
pop %rbx #pop fp
addq $8, %rsp #remove args from stack
cmp $0, %eax #compare result to 0
je L17 #jump to L17 if false
movq -8(%rbx), %rax #move n to rax
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $1, %rax #move num to rax
push %rax
movq -24(%rbx), %rax #move cur to rax
add 0(%rsp), %rax
add $8, %rsp #pop the stack
movq %rax, -24(%rbx) #move rax into position
L17:
movq $1, %rax #move num to rax
push %rax
movq -8(%rbx), %rax #move n to rax
add 0(%rsp), %rax
add $8, %rsp #pop the stack
movq %rax, -8(%rbx) #move rax into position
jmp L13 #jump to L13 to continue while loop
L14:
addq $24, %rsp #remove local vars
ret #return
