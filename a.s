.section .rodata
.WriteIntString: .string "%d "
.WritelnString: .string "\n"
.WriteStringString: .string "%s"
.s0: .string "Testing..."
.comm x, 8, 32
.text
.globl main
test:
movq $.s0, %rsi #move .s0 into 2nd arg to prepare for printing
movq $.WriteStringString, %rdi #prepare to write a string
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
ret #return
test2:
movq $4, %rax #move num to rax
push %rax
movq $3, %rax #move num to rax
add 0(%rsp), %rax
add $8, %rsp #pop the stack
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
ret #return
main:
GOT A DEC: var dec
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
GOT A DEC: var dec
movq %rax, %rsi #move num into 2nd arg to prepare for printing
movq $.WriteIntString, %rdi #prepare to write an int
movl $0, %eax #reset ret
call printf
movq $.WritelnString, %rdi #prepare to write a new line
movl $0, %eax #reset ret
call printf
ret #return
