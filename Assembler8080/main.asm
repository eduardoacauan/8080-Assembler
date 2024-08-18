         ORG    0x8000 ; diz para o assembler usar este endereço para calcular as labels
         LXI    H, nome ; agora o registrador par HL aponta para o endereço de nome
    loop MOV    A, (HL) ; move o conteudo apontado por HL para o registrador A
         ORA    A       ; faz OR bit a bit com o registrador A
         JZ     end     ; caso seja zero pula para o fim, se não...
         ANI    0xDF    ; faz um AND com uma mascara de bits para que seja possivel desligar o 5 bit, resultando na capitalização da letra
         INX    H       ; incrementa o registrador par HL para que aponte para o proximo byte na memoria
         JMP   loop     ; volta para o loop
    end  RET            ; retorna da rotina.
    nome byte "Eduardo", 0