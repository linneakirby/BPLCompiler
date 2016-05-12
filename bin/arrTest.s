.section .rodata
.WriteIntString: .string "%d "
.WritelnString: .string "\n"
.WriteStringString: .string "%s"
.ReadIntString: .string "%d"
.s0: .string "i is: "
.s1: .string "RETURNED: "
.text
.globl main
test:
movq %rsp, %rbx #move stack pointer to fp
subq $8, %rsp #decrement stack pointer by 8 to make room for local vars
movq $0, %rax #move num to rax
movq %rax, -8(%rbx) #move rax into position
L0:
movq -8(%rbx), %rax #move i to rax
push %rax
movq $10, %rax #move num to rax
cmp %eax, 0(%rsp) #relop compare
jge L2
mov $1, %rax #condition is true
jmp L3
L2:
mov $0, %rax #condition is false
L3:
add $8, %rsp #pop the stack
cmp $0, %eax #compare result to 0
je L1 #jump to L1 if false
movq $.s0, %rax #move string literal to rax
movq %rax, %rsi #move rax into 2nd arg to prepare for printing
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
movq $5, %rax #move num to rax
push %rax
movq -8(%rbx), %rax #move i to rax
add 0(%rsp), %rax
add $8, %rsp #pop the stack
movq %rax, %r10 #move result of expression to r10
movq -8(%rbx), %rax #move i to rax
imul $8, %eax #find index
addq 16(%rbx), %rax #find address of element
movq %r10, 0(%rax) #move value to place in array
movq -8(%rbx), %rax #move i to rax
imul $8, %eax #find offset
addq 16(%rbx), %rax #find address of element
movq 0(%rax), %rax #set value of array at offset
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
movq $1, %rax #move num to rax
push %rax
movq -8(%rbx), %rax #move i to rax
add 0(%rsp), %rax
add $8, %rsp #pop the stack
movq %rax, -8(%rbx) #move rax into position
jmp L0 #jump to L0 to continue while loop
L1:
movq $9, %rax #move num to rax
imul $8, %eax #find offset
addq 16(%rbx), %rax #find address of element
movq 0(%rax), %rax #set value of array at offset
addq $8, %rsp #restoring stack to original size
ret #return
addq $8, %rsp #remove local vars
ret #return
main:
movq %rsp, %rbx #move stack pointer to fp
subq $88, %rsp #decrement stack pointer by 88 to make room for local vars
movq %rbx, %rax #move fp to rax
addq $-88, %rax
push %rax #push arg onto stack
push %rbx #push fp
call test #call test
pop %rbx #pop fp
addq $8, %rsp #remove args from stack
movq %rax, -8(%rbx) #move rax into position
movq $.s1, %rax #move string literal to rax
movq %rax, %rsi #move rax into 2nd arg to prepare for printing
movq $.WriteStringString, %rdi #prepare to write a string
movl $0, %eax #reset ret
call printf
movq -8(%rbx), %rax #move a to rax
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
addq $88, %rsp #remove local vars
ret #return
