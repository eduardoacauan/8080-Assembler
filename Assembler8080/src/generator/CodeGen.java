package generator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import tokens.TokenType;
import java.util.HashMap;

import ast.*;
/***********************************
 * This class is responsible for   *
 * generating the machine code     *
 * and writing it into a file (bin)*
 * by: Eduardo S. Acauan           *
 **********************************/
public class CodeGen {
	private List<AST>  trees;
	private List<Byte> program;
	private int 	   pc;
	private int 	   origin;
	private boolean    error;
	private boolean    hasReference;
	private int 	   pos;
	private int        pass;
	private HashMap<String, Integer> labels;
	
	public CodeGen(List<AST> trees) {
		this.trees = trees;
		program = new ArrayList<>();
		
		origin = 0;
		pc     = 0;
		pos    = 0;
		error  = false;
		hasReference = false;
		pass = 1;
		
		labels = new HashMap<>();
	}
	
	public void generate(String path) throws IOException {
		
		while(!endStream()) {
			switch(peek().getType()) {
				case AType.MNEMONIC:
					handleMnemonic((Mnemonic)peek());
					break;
				case AType.LABEL:
					handleLabel();
					break;
				case AType.BYTEDECLARATION:
					handleByte((ByteDecl)peek());
					break;
				case AType.WORDDECLARATION:
					handleWord((WordDecl)peek());
					break;
				case AType.IDENTIFIER:
					handleIDasLB();
					break;
				default:
					error("Invalid operation!");
					break;
			}
			next();
		}
		
		if(error)
			return;
		
		if(hasReference && pass < 2) {
			pass++;
			origin = 0;
			pos = 0;
			pc = 0;
			hasReference = false;
			program.clear();
			generate(path);
		}
	}
	
	private void handleMnemonic(Mnemonic m) {
		switch(m.getIns()) {
			case TokenType.TK_RET:
				write8(0xC9);
				return;
			case TokenType.TK_NOP:
				write8(0x00);
				return;
			case TokenType.TK_RLC:
				write8(0x07);
				return;
			case TokenType.TK_RAL:
				write8(0x17);
				return;
			case TokenType.TK_DAA:
				write8(0x27);
				return;
			case TokenType.TK_STC:
				write8(0x37);
				return;
			case TokenType.TK_RAR:
				write8(0x0F);
				return;
			case TokenType.TK_RRC:
				write8(0x1F);
				return;
			case TokenType.TK_CMC:
				write8(0x2F);
				return;
			case TokenType.TK_CMA:
				write8(0x3F);
				return;
			case TokenType.TK_RNZ:
				write8(0xC0);
				return;
			case TokenType.TK_RNC:
				write8(0xD0);
				return;
			case TokenType.TK_RPO:
				write8(0xE0);
				return;
			case TokenType.TK_RZ:
				write8(0xC8);
				return;
			case TokenType.TK_RC:
				write8(0xD8);
				return;
			case TokenType.TK_RPE:
				write8(0xE8);
				return;
			case TokenType.TK_RM:
				write8(0xF8);
				return;
			case TokenType.TK_RPLUS:
				write8(0xF0);
				return;
			case TokenType.TK_HLT:
				write8(0x76);
				return;
			case TokenType.TK_DI:
				write8(0xF3);
				return;
			case TokenType.TK_XCHG:
				write8(0xEB);
				return;
			case TokenType.TK_XTHL:
				write8(0xE3);
				return;
			case TokenType.TK_PCHL:
				write8(0xE9);
				return;
			case TokenType.TK_SPHL:
				write8(0xF9);
				return;
			case TokenType.TK_EI:
				write8(0xFB);
				return;
			case TokenType.TK_MVI:
				mvi(m);
				return;
			case TokenType.TK_JMP:
				jmp(m);
				return;
			case TokenType.TK_ORG:
				org(m);
				return;
			case TokenType.TK_LXI:
				lxi(m);
				return;
			case TokenType.TK_STAX:
				stax(m);
				return;
			case TokenType.TK_STA:
				sta(m);
				return;
			case TokenType.TK_SHLD:
				shld(m);
				return;
			case TokenType.TK_INX:
				inx(m);
				return;
			case TokenType.TK_INR:
				inr(m);
				break;
			case TokenType.TK_DCR:
				dcr(m);
				break;
			case TokenType.TK_LDAX:
				ldax(m);
				break;
			case TokenType.TK_DAD:
				dad(m);
				break;
			case TokenType.TK_DCX:
				dcx(m);
				break;
			case TokenType.TK_MOV:
				mov(m);
				break;
			case TokenType.TK_ADD:
				add(m);
				break;
			case TokenType.TK_ADC:
				adc(m);
				break;
			case TokenType.TK_SUB:
				sub(m);
				break;
			case TokenType.TK_SBB:
				sbb(m);
				break;
			case TokenType.TK_XRA:
				xra(m);
				break;
			case TokenType.TK_ANA:
				ana(m);
				break;
			case TokenType.TK_ORA:
				ora(m);
				break;
			case TokenType.TK_CMP:
				cmp(m);
				break;
			case TokenType.TK_JZ:
				jz(m);
				break;
			case TokenType.TK_JNZ:
				jnz(m);
				break;
			case TokenType.TK_JNC:
				jnc(m);
				break;
			case TokenType.TK_JPO:
				jpo(m);
				break;
			case TokenType.TK_JP:
				jp(m);
				break;
			case TokenType.TK_POP:
				pop(m);
				break;
			case TokenType.TK_PUSH:
				pop(m);
				break;
			case TokenType.TK_OUT:
				out(m);
				break;
			case TokenType.TK_CNZ:
				cnz(m);
				break;
			case TokenType.TK_CNC:
				cnc(m);
				break;
			case TokenType.TK_CPO:
				cpo(m);
				break;
			case TokenType.TK_CPLUS:
				cplus(m);
				break;
			case TokenType.TK_ADI:
				adi(m);
				break;
			case TokenType.TK_SUI:
				sui(m);
				break;
			case TokenType.TK_ORI:
				adi(m);
				break;
			case TokenType.TK_ANI:
				ani(m);
				break;
			case TokenType.TK_ACI:
				aci(m);
				break;
			case TokenType.TK_SBI:
				sbi(m);
				break;
			case TokenType.TK_XRI:
				xri(m);
				break;
			case TokenType.TK_CPI:
				cpi(m);
				break;
			case TokenType.TK_RST:
				rst(m);
				break;
			case TokenType.TK_IN:
				in(m);
				break;
			case TokenType.TK_CZ:
				cz(m);
				break;
			case TokenType.TK_CC:
				cc(m);
				break;
			case TokenType.TK_CPE:
				cpe(m);
				break;
			case TokenType.TK_CM:
				cm(m);
				break;
			case TokenType.TK_CALL:
				call(m);
				break;
			default:
				break;
		}
	}
	
	private void cpi(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int value = extractValue(m.getArg1());
		
		write8(0xFE);
		write8(value);
	}
	
	private void xri(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int value = extractValue(m.getArg1());
		
		write8(0xEE);
		write8(value);
	}
	
	private void sbi(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int value = extractValue(m.getArg1());
		
		write8(0xDE);
		write8(value);
	}
	
	private void aci(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int value = extractValue(m.getArg1());
		
		write8(0xCE);
		write8(value);
	}
	
	private void call(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xCD);
		write16(addr);
	}
	
	private void rst(Mnemonic m) {
		if(!validLiteral(m.getArg1())){
			error("Value expected!");
			return;
		}
		
		int value = extractValue(m.getArg1());
		
		switch(value) {
			case 0x00:
				write8(0xC7);
				return;
			case 0x01:
				write8(0xCF);
				return;
			case 0x02:
				write8(0xD7);
				return;
			case 0x03:
				write8(0xDF);
				return;
			case 0x04:
				write8(0xE7);
				return;
			case 0x05:
				write8(0xEF);
				return;
			case 0x06:
				write8(0xF7);
				return;
			case 0x07:
				write8(0xFF);
				return;
			default:
				error("Invalid value! use 0 - 7 !");
				return;
		}
	}
	
	private void cm(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xFC);
		write16(addr);
	}
	
	private void cpe(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xEC);
		write16(addr);
	}
	
	private void cc(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xDC);
		write16(addr);
	}
	
	private void cz(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xCC);
		write16(addr);
	}
	
	private void ori(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int value = extractValue(m.getArg1());
		
		write8(0xF6);
		write8(value);
	}
	
	private void ani(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int value = extractValue(m.getArg1());
		
		write8(0xE6);
		write8(value);
	}
	
	private void sui(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int value = extractValue(m.getArg1());
		
		write8(0xD6);
		write8(value);
	}
	
	private void adi(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int value = extractValue(m.getArg1());
		
		write8(0xC6);
		write8(value);
	}
	
	private void cplus(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xF4);
		write16(addr);
	}
	
	private void cpo(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xE4);
		write16(addr);
	}
	
	private void cnc(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xD4);
		write16(addr);
	}
	
	private void cnz(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xC4);
		write16(addr);
	}
	
	private void in(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid value expected!");
			return;
		}
		
		write8(0xDB);
		write8(extractValue(m.getArg1()));
	}
	
	private void out(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid value expected!");
			return;
		}
		
		write8(0xD3);
		write8(extractValue(m.getArg1()));
	}
	
	private void jp(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xF2);
		write16(addr);
	}
	
	private void jpo(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xE2);
		write16(addr);
	}
	
	private void jnc(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xD2);
		write16(addr);
	}
	
	private void jnz(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xC2);
		write16(addr);
	}
	
	private void push(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0xC5);
				return;
			case TokenType.TK_D:
				write8(0xD5);
				return;
			case TokenType.TK_H:
				write8(0xE5);
				return;
			case TokenType.TK_PSW:
				write8(0xF5);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void pop(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0xC1);
				return;
			case TokenType.TK_D:
				write8(0xD1);
				return;
			case TokenType.TK_H:
				write8(0xE1);
				return;
			case TokenType.TK_PSW:
				write8(0xF1);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void jm(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xFA);
		write16(addr);
	}
	
	private void jpe(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xEA);
		write16(addr);
	}
	
	private void jc(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xDA);
		write16(addr);
	}
	
	private void jz(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xCA);
		write16(addr);
	}
	
	private void cmp(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0xB8);
				return;
			case TokenType.TK_C:
				write8(0xB9);
				return;
			case TokenType.TK_D:
				write8(0xBA);
				return;
			case TokenType.TK_E:
				write8(0xBB);
				return;
			case TokenType.TK_H:
				write8(0xBC);
				return;
			case TokenType.TK_L:
				write8(0xBD);
				return;
			case TokenType.TK_M:
				write8(0xBE);
				return;
			case TokenType.TK_A:
				write8(0xBF);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void ora(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0xB0);
				return;
			case TokenType.TK_C:
				write8(0xB1);
				return;
			case TokenType.TK_D:
				write8(0xB2);
				return;
			case TokenType.TK_E:
				write8(0xB3);
				return;
			case TokenType.TK_H:
				write8(0xB4);
				return;
			case TokenType.TK_L:
				write8(0xB5);
				return;
			case TokenType.TK_M:
				write8(0xB6);
				return;
			case TokenType.TK_A:
				write8(0xB7);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void xra(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0xA8);
				return;
			case TokenType.TK_C:
				write8(0xA9);
				return;
			case TokenType.TK_D:
				write8(0xAA);
				return;
			case TokenType.TK_E:
				write8(0xAB);
				return;
			case TokenType.TK_H:
				write8(0xAC);
				return;
			case TokenType.TK_L:
				write8(0xAD);
				return;
			case TokenType.TK_M:
				write8(0xAE);
				return;
			case TokenType.TK_A:
				write8(0xAF);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void ana(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0xA0);
				return;
			case TokenType.TK_C:
				write8(0xA1);
				return;
			case TokenType.TK_D:
				write8(0xA2);
				return;
			case TokenType.TK_E:
				write8(0xA3);
				return;
			case TokenType.TK_H:
				write8(0xA4);
				return;
			case TokenType.TK_L:
				write8(0xA5);
				return;
			case TokenType.TK_M:
				write8(0xA6);
				return;
			case TokenType.TK_A:
				write8(0xA7);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void sbb(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x98);
				return;
			case TokenType.TK_C:
				write8(0x99);
				return;
			case TokenType.TK_D:
				write8(0x9A);
				return;
			case TokenType.TK_E:
				write8(0x9B);
				return;
			case TokenType.TK_H:
				write8(0x9C);
				return;
			case TokenType.TK_L:
				write8(0x9D);
				return;
			case TokenType.TK_M:
				write8(0x9E);
				return;
			case TokenType.TK_A:
				write8(0x9F);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void adc(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x88);
				return;
			case TokenType.TK_C:
				write8(0x89);
				return;
			case TokenType.TK_D:
				write8(0x8A);
				return;
			case TokenType.TK_E:
				write8(0x8B);
				return;
			case TokenType.TK_H:
				write8(0x8C);
				return;
			case TokenType.TK_L:
				write8(0x8D);
				return;
			case TokenType.TK_M:
				write8(0x8E);
				return;
			case TokenType.TK_A:
				write8(0x8F);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void sub(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x90);
				return;
			case TokenType.TK_C:
				write8(0x91);
				return;
			case TokenType.TK_D:
				write8(0x92);
				return;
			case TokenType.TK_E:
				write8(0x93);
				return;
			case TokenType.TK_H:
				write8(0x94);
				return;
			case TokenType.TK_L:
				write8(0x95);
				return;
			case TokenType.TK_M:
				write8(0x96);
				return;
			case TokenType.TK_A:
				write8(0x97);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void add(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x80);
				return;
			case TokenType.TK_C:
				write8(0x81);
				return;
			case TokenType.TK_D:
				write8(0x82);
				return;
			case TokenType.TK_E:
				write8(0x83);
				return;
			case TokenType.TK_H:
				write8(0x84);
				return;
			case TokenType.TK_L:
				write8(0x85);
				return;
			case TokenType.TK_M:
				write8(0x86);
				return;
			case TokenType.TK_A:
				write8(0x87);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void mov(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected!");
			return;
		}
		
		Register r = (Register)m.getArg1();
		Register r2= (Register)m.getArg2();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				mov_b(r2);
				return;
			case TokenType.TK_C:
				mov_c(r2);
				return;
			case TokenType.TK_D:
				mov_d(r2);
				return;
			case TokenType.TK_E:
				mov_e(r2);
				return;
			case TokenType.TK_H:
				mov_h(r2);
				return;
			case TokenType.TK_L:
				mov_l(r2);
				return;
			case TokenType.TK_M:
				mov_m(r2);
				return;
			case TokenType.TK_A:
				mov_a(r2);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void mov_a(Register r) {
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x78);
				return;
			case TokenType.TK_C:
				write8(0x79);
				return;
			case TokenType.TK_D:
				write8(0x7A);
				return;
			case TokenType.TK_E:
				write8(0x7B);
				return;
			case TokenType.TK_H:
				write8(0x7C);
				return;
			case TokenType.TK_L:
				write8(0x7D);
				return;
			case TokenType.TK_M:
				write8(0x7E);
				return;
			case TokenType.TK_A:
				write8(0x7F);
				return;
			default:
				error("invalid register");
				break;
		}
	}
	
	private void mov_m(Register r) {
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x70);
				return;
			case TokenType.TK_C:
				write8(0x71);
				return;
			case TokenType.TK_D:
				write8(0x72);
				return;
			case TokenType.TK_E:
				write8(0x73);
				return;
			case TokenType.TK_H:
				write8(0x74);
				return;
			case TokenType.TK_L:
				write8(0x75);
				return;
			case TokenType.TK_A:
				write8(0x77);
				return;
			default:
				error("invalid register");
				break;
		}
	}
	
	private void mov_c(Register r) {
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x48);
				return;
			case TokenType.TK_C:
				write8(0x49);
				return;
			case TokenType.TK_D:
				write8(0x4A);
				return;
			case TokenType.TK_E:
				write8(0x4B);
				return;
			case TokenType.TK_H:
				write8(0x4C);
				return;
			case TokenType.TK_L:
				write8(0x4D);
				return;
			case TokenType.TK_M:
				write8(0x4E);
				return;
			case TokenType.TK_A:
				write8(0x4F);
				return;
			default:
				error("invalid register");
				break;
		}
	}
	
	private void mov_d(Register r) {
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x50);
				return;
			case TokenType.TK_C:
				write8(0x51);
				return;
			case TokenType.TK_D:
				write8(0x52);
				return;
			case TokenType.TK_E:
				write8(0x53);
				return;
			case TokenType.TK_H:
				write8(0x54);
				return;
			case TokenType.TK_L:
				write8(0x55);
				return;
			case TokenType.TK_M:
				write8(0x56);
				return;
			case TokenType.TK_A:
				write8(0x57);
				return;
			default:
				error("invalid register");
				break;
		}
	}
	
	private void mov_e(Register r) {
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x58);
				return;
			case TokenType.TK_C:
				write8(0x59);
				return;
			case TokenType.TK_D:
				write8(0x5A);
				return;
			case TokenType.TK_E:
				write8(0x5B);
				return;
			case TokenType.TK_H:
				write8(0x5C);
				return;
			case TokenType.TK_L:
				write8(0x5D);
				return;
			case TokenType.TK_M:
				write8(0x5E);
				return;
			case TokenType.TK_A:
				write8(0x5F);
				return;
			default:
				error("invalid register");
				break;
		}
	}
	
	private void mov_h(Register r) {
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x60);
				return;
			case TokenType.TK_C:
				write8(0x61);
				return;
			case TokenType.TK_D:
				write8(0x62);
				return;
			case TokenType.TK_E:
				write8(0x63);
				return;
			case TokenType.TK_H:
				write8(0x64);
				return;
			case TokenType.TK_L:
				write8(0x65);
				return;
			case TokenType.TK_M:
				write8(0x66);
				return;
			case TokenType.TK_A:
				write8(0x67);
				return;
			default:
				error("invalid register");
				break;
		}
	}
	
	private void mov_l(Register r) {
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x68);
				return;
			case TokenType.TK_C:
				write8(0x69);
				return;
			case TokenType.TK_D:
				write8(0x6A);
				return;
			case TokenType.TK_E:
				write8(0x6B);
				return;
			case TokenType.TK_H:
				write8(0x6C);
				return;
			case TokenType.TK_L:
				write8(0x6D);
				return;
			case TokenType.TK_M:
				write8(0x6E);
				return;
			case TokenType.TK_A:
				write8(0x6F);
				return;
			default:
				error("invalid register");
				break;
		}
	}
	
	private void mov_b(Register r) {
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x40);
				return;
			case TokenType.TK_C:
				write8(0x41);
				return;
			case TokenType.TK_D:
				write8(0x42);
				return;
			case TokenType.TK_E:
				write8(0x43);
				return;
			case TokenType.TK_H:
				write8(0x44);
				return;
			case TokenType.TK_L:
				write8(0x45);
				return;
			case TokenType.TK_M:
				write8(0x46);
				return;
			case TokenType.TK_A:
				write8(0x47);
				return;
			default:
				error("invalid register");
				break;
		}
	}
	
	private void dad(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected!");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x09);
				break;
			case TokenType.TK_D:
				write8(0x19);
				break;
			case TokenType.TK_H:
				write8(0x29);
				break;
			case TokenType.TK_SP:
				write8(0x39);
				break;
			default:
				error("Invalid register! use b, d, h or sp!");
				return;
		}
	}
	
	private void dcx(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x0B);
				return;
			case TokenType.TK_D:
				write8(0x1B);
				return;
			case TokenType.TK_H:
				write8(0x2B);
				return;
			case TokenType.TK_SP:
				write8(0x3B);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void ldax(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected!");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		if(r.getRG() == TokenType.TK_B) {
			write8(0x0A);
			return;
		}
		
		if(r.getRG() == TokenType.TK_D) {
			write8(0x1A);
			return;
		}
		
		error("Invalid register at ldax instruction! use b or d!");
	}
	
	private void dcr(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x05);
				return;
			case TokenType.TK_C:
				write8(0x0D);
				return;
			case TokenType.TK_D:
				write8(0x15);
				return;
			case TokenType.TK_E:
				write8(0x1C);
				return;
			case TokenType.TK_H:
				write8(0x25);
				return;
			case TokenType.TK_L:
				write8(0x2C);
				return;
			case TokenType.TK_SP:
				write8(0x35);
				return;
			case TokenType.TK_A:
				write8(0x3C);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void inr(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x04);
				return;
			case TokenType.TK_C:
				write8(0x0C);
				return;
			case TokenType.TK_D:
				write8(0x14);
				return;
			case TokenType.TK_E:
				write8(0x1C);
				return;
			case TokenType.TK_H:
				write8(0x24);
				return;
			case TokenType.TK_L:
				write8(0x2C);
				return;
			case TokenType.TK_SP:
				write8(0x34);
				return;
			case TokenType.TK_A:
				write8(0x3C);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void inx(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x03);
				return;
			case TokenType.TK_D:
				write8(0x13);
				return;
			case TokenType.TK_H:
				write8(0x23);
				return;
			case TokenType.TK_SP:
				write8(0x33);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private void shld(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid value expected!");
			return;
		}
		
		write8(0x22);
		write16(extractValue(m.getArg1()));
	}
	
	private void sta(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid value expected!");
			return;
		}
		
		write8(0x32);
		write16(extractValue(m.getArg1()));
	}
	
	private void stax(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected!");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		if(r.getRG() == TokenType.TK_B) {
			write8(0x02);
			return;
		}
		
		if(r.getRG() == TokenType.TK_D) {
			write8(0x12);
			return;
		}
		
		error("Invalid register at stax instruction! use b or d!");
	}
	
	private void lxi(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Register expected!");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		if(!validLiteral(m.getArg2())) {
			error("Valid value expected!");
			return;
		}
		
		int value = extractValue(m.getArg2());
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x06);
				break;
			case TokenType.TK_D:
				write8(0x16);
				break;
			case TokenType.TK_H:
				write8(0x26);
				break;
			case TokenType.TK_SP:
				write8(0x36);
				break;
			default:
				error("Invalid register! use b, d, h or sp!");
				return;
		}
		
		write16(value);
	}
	
	private void jmp(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid address expected!");
			return;
		}
		
		int addr = extractValue(m.getArg1());
		
		write8(0xC3);
		write16(addr);
	}
	
	private void org(Mnemonic m) {
		if(!validLiteral(m.getArg1())) {
			error("Valid literal expected at ORG declarative!");
			return;
		}
		
		origin = extractValue(m.getArg1());
	}
	
	private void handleByte(ByteDecl decl) {
		for(var i : decl.getBytes()) {
			if(i == null) {
				error("error at byte declaration!");
				return;
			}
			if(i.getType() == AType.STRING) {
				for(int j = 0; j < ((StringTree)i).getID().length(); j++)
					write8((int)((StringTree)i).getID().charAt(j));
				continue;
			}
			if(!validLiteral(i)) {
				error("Invalid value on byte declaration!");
				continue;
			}
			
			write8(extractValue(i));
		}
	}
	
	private void handleWord(WordDecl decl) {
		for(var i : decl.getWords()) {
			if(i == null) {
				error("error at word declaration!");
				return;
			}
			if(i.getType() == AType.STRING) {
				for(int j = 0; j < ((StringTree)i).getID().length(); j++)
					write16((int)((StringTree)i).getID().charAt(j));
				continue;
			}
			if(!validLiteral(i)) {
				error("Invalid value on byte declaration!");
				continue;
			}
			
			write16(extractValue(i));
		}
	}
	
	private void handleLabel() {
		Label lb = (Label)peek();
		labels.put(lb.getID(), pc + origin);
	}
	
	private void handleIDasLB() {
		Identifier id = (Identifier)peek();
		
		labels.put(id.getID(), pc + origin);
	}
	
	public void WriteToFile(String path) throws IOException {
		StringBuilder name = new StringBuilder(getFilename(path));
		
		name.append(".bin");
		
		File file = new File(name.toString());
		
		FileOutputStream fw = new FileOutputStream(file);
		
		for(var x : program) {
			fw.write(new byte[] {x});
		}
		
		fw.close();
	}
	
	public List<Byte> getProgram(){
		return program;
	}
	
	public boolean getError() {
		return error;
	}
	
	private void mvi(Mnemonic m) {
		if(m.getArg1().getType() != AType.REGISTER) {
			error("Invalid left hand side in MVI instruction! Register expected");
			return;
		}
		
		Register r = (Register)m.getArg1();
		
		if(!validLiteral(m.getArg2())) {
			error("Invalid right hand side in MVI instruction! literal expected");
			return;
		}
		
		int value = extractValue(m.getArg2());
		
		if(value > 0xFF) {
			warning("Value " + value + " is bigger than 1 byte !");
			value &= 0xFF;
		}
		
		switch(r.getRG()) {
			case TokenType.TK_B:
				write8(0x06);
				write8(value);
				return;
			case TokenType.TK_D:
				write8(0x16);
				write8(value);
				return;
			case TokenType.TK_H:
				write8(0x26);
				write8(value);
				return;
			case TokenType.TK_M:
				write8(0x36);
				write8(value);
				return;
			case TokenType.TK_C:
				write8(0x0E);
				write8(value);
				return;
			case TokenType.TK_E:
				write8(0x1E);
				write8(value);
				return;
			case TokenType.TK_L:
				write8(0x2E);
				write8(value);
				return;
			case TokenType.TK_A:
				write8(0x3E);
				write8(value);
				return;
			default:
				error("Invalid register");
				return;
		}
	}
	
	private AST peek() {
		return trees.get(pos);
	}
	
	private void next() {
		pos++;
	}
	
	private void write8(int value) {
		program.add((byte)value);
		pc++;
	}
	
	private void write16(int value) {
		program.add((byte)(value & 0xFF));
		program.add((byte)((value >> 8) & 0xFF));
		pc += 2;
	}
	
	private boolean endStream() {
		return pos >= trees.size();
	}
	
	private void error(String args) {
		System.out.print("Error(" + peek().getLine() + "): " + args);
		System.out.print("\n");
		error = true;
		return;
	}
	
	private void warning(String args) {
		System.out.print("Warning(" + peek().getLine() + "): " + args);
		System.out.print("\n");
		return;
	}
	
	private int extractValue(AST arg) {
		switch(arg.getType()) {
			case AType.LITERAL:
				return ((Literal)arg).getValue();
			case AType.IDENTIFIER:
				Identifier i = (Identifier)arg;
				
				if(labels.containsKey(i.getID())) {
					Object o = labels.get(i.getID());
					
					if(o == null)
						return -1;
					return (int)o;
				}
				hasReference = true;
				return -1;
			case AType.BINARYEXPR:
				return calculate((BinaryExpr)arg);
				
			default:
				error("Invalid value!");
				return -1;
		}
	}
	
	private int calculate(BinaryExpr bin) {
		
		int l = extractValue(bin.getLeft());
		int r = extractValue(bin.getRight());
		
		switch(bin.getOP()) {
			case TokenType.TK_PLUS:
				return l + r;
			case TokenType.TK_MINUS:
				return l - r;
			case TokenType.TK_MUL:
				return l * r;
			case TokenType.TK_DIV:
				if(r == 0) {
					error("Cannot divide by zero!");
					return -1;
				}
				return l + r;
			default:
				error("Invalid operation!");
				return -1;
		}
	}
	
	private boolean validLiteral(AST arg) {
		if(arg.getType() != AType.LITERAL && arg.getType() != AType.BINARYEXPR 
		  && arg.getType() != AType.IDENTIFIER)
			return false;
		return true;
	}
	
	private String getFilename(String path) {
		int pos = path.lastIndexOf(".");
		
		if(pos > 0 && pos < (path.length() - 1))
			return path.substring(0, pos);
		return null;
	}
}
