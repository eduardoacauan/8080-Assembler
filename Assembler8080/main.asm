/*******************
* Programa Teste   *
* Autor: Eduardo A *
********************/
         ORG   0x8000 //endereco 0x8000
         MVI   A, 'a'
         ANI   0xDF
         LXI   H, 3 * 3
         MOV   (HL), A // move o conteudo do registrador A para o endereço apontado por HL. (tambem pode ser escrito como MOV M, A)
         RET
; fim