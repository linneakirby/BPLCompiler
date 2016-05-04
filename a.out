.section .rodata
.WriteIntString: .string "%d "
.WritelnString: .string "\n"
.WriteStringString: .string "%s"
.text
.globl main
main:
movq $5, %rax
movq $.WriteIntString, %rdi //prepare to write an int
movl $0, %eax //reset ret
call printf
movq $5, %rax
movq $.WriteIntString, %rdi //prepare to write an int
movl $0, %eax //reset ret
call printf
