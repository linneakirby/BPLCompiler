.section .rodata
.WriteIntString: .string "%d "
.WritelnString: .string "\n"
.WriteStringString: .string "%s"
.ReadIntString: .string "%d"
.s0: .string "potato"
.s1: .string "cat"
.s2: .string "hedgehog"
.s3: .string "banana"
.text
.globl main
s:
movq %rsp, %rbx #move stack pointer to fp
subq $0, %rsp #decrement stack pointer by 0 to make room for local vars
movq $.s0, %rax #move string literal to rax
movq %rax, %r10 #move result of expression to r10
movq $0, %rax #move num to rax
imul $8, %eax #find index
addq 16(%rbx), %rax #find address of element
movq %r10, 0(%rax) #move value to place in array
movq $.s1, %rax #move string literal to rax
movq %rax, %r10 #move result of expression to r10
movq $1, %rax #move num to rax
imul $8, %eax #find index
addq 16(%rbx), %rax #find address of element
movq %r10, 0(%rax) #move value to place in array
movq $.s2, %rax #move string literal to rax
movq %rax, %r10 #move result of expression to r10
movq $2, %rax #move num to rax
imul $8, %eax #find index
addq 16(%rbx), %rax #find address of element
movq %r10, 0(%rax) #move value to place in array
movq $.s3, %rax #move string literal to rax
movq %rax, %r10 #move result of expression to r10
movq $3, %rax #move num to rax
imul $8, %eax #find index
addq 16(%rbx), %rax #find address of element
movq %r10, 0(%rax) #move value to place in array
addq $0, %rsp #remove local vars
ret #return
main:
movq %rsp, %rbx #move stack pointer to fp
subq $32, %rsp #decrement stack pointer by 32 to make room for local vars
movq %rbx, %rax #move fp to rax
addq $-32, %rax
push %rax #push arg onto stack
push %rbx #push fp
call s #call s
pop %rbx #pop fp
addq $8, %rsp #remove args from stack
movq $2, %rax #move num to rax
imul $8, %eax #find offset
addq %rbx, %rax #add fp to offset
addq $-32, %rax #find position
movq 0(%rax), %rax #set value of array at offset
movq %rax, %rsi #move rax into 2nd arg to prepare for printing
movq $.WriteStringString, %rdi #prepare to write a string
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
movq $0, %rax #move num to rax
imul $8, %eax #find offset
addq %rbx, %rax #add fp to offset
addq $-32, %rax #find position
movq 0(%rax), %rax #set value of array at offset
movq %rax, %rsi #move rax into 2nd arg to prepare for printing
movq $.WriteStringString, %rdi #prepare to write a string
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
addq $32, %rsp #remove local vars
ret #return
