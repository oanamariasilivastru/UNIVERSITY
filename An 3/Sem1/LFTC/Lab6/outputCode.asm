bits 32
global start

extern scanf
extern printf
extern exit

section .data
	a dd 4
	b dd 0
	c dd 0
	d dd 0
	format db "%d", 0

section .text
start:
	push 4
	push 0
	pop dword [b]
	pop dword [a]
	push dword a
	push dword format
	call scanf
	add esp, 8

	push dword b
	push dword format
	call scanf
	add esp, 8

	push dword c
	push dword format
	call scanf
	add esp, 8

	push dword [a]
	push dword [b]
	push dword [c]
	push 2
	push 5
	pop edx
	pop eax
	imul eax, edx
	push eax
	
	push 1
	pop edx
	pop eax
	sub eax, edx
	push eax
	
	pop edx
	pop eax
	add eax, edx
	push eax
	
	pop edx
	pop eax
	add eax, edx
	push eax
	
	pop edx
	pop eax
	add eax, edx
	push eax
	
	pop dword [d]

	push dword [d]
	push dword format
	call printf
	add esp, 8

	push dword 0
	call exit
