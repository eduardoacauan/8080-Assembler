toUpper:
	POP	H
LOOP:	MOV	A, (HL)
	ORA	A
	JZ	END
	ANI	0xDF
	MOV	(HL), A
	INX	H
	JMP	LOOP
END:	RET