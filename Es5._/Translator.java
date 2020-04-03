import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count = 0;
    int operands = 0;

    public Translator(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }
    
	void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
		throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
		if (look.tag == t) {
			if (look.tag != Tag.EOF) move();
		} else error("syntax error");
    }

    public void prog() {        
	// ... completare ...
        int lnext_prog = code.newLabel();
        stat(lnext_prog);
        code.emitLabel(lnext_prog);
        match(Tag.EOF);
        try {
         code.toJasmin();
        }
        catch(java.io.IOException e) {
         System.out.println("IO error\n");
        };
	// ... completare ...
    }
    
    private void statlist(int lnext) {
	if(look.tag == '(') {
	int lnext = code.newLabel();
    	stat(lnext);
	code.emitLabel(lnext);
    	statlistp();
	}
    }
    
    private void statlistp(int lnext) {
    	if(look.tag == '(') {
	int lnext = code.newLabel();
    	stat(lnext);
	code.emitLabel(lnext);
    	statlistp();
	}else error("Error in statlistp");
    }
    
    private void stat(int lnext) {
    	if(look.tag == '(') {
	move();
	statp(lnext);				// label sistemate (?)
	match(')');
    	}else error("Error in stat");
    }
    

    public void statp(int lnext) {
        switch(look.tag) {
    		case '=':
    			move();
    			if(look.tag == Tag.ID) {
			int read_id_addr = st.lookupAddress(((Word)look).lexeme);
			if(read_id_addr == -1){
			read_id_addr = count;
			st.insert(((Word)look).lexeme,count++);
			}
			expr();
			code.emit(OpCode.istore, read_id_addr);
				}else error("Error in value assignment");
    			break;
    		case Tag.COND:	// il codice utile sta in bexpr
    			move();
    			//lnext = code.newLabel();	// creo una label da mettere alla fine del vero
			int tru = code.newLabel();
			int fals = code.newLabel();
    			bexpr(tru, fals);	
			code.emitLabel(tru);    			
			stat(lnext);
    			code.emit(OpCode.GOto, lnext);	// dopo il vero mando alla fine del cond
    			code.emitLabel(fals);
    			elseopt(lnext);
    			break;
    		case Tag.WHILE:
			move();
    			int body = code.newLabel();
			int succ = code.newLabel();
    			code.emitLabel(body);	// metto una label che serve esclusivamente per tornare alla condizione dello while
    			bexpr(succ, lnext);	// - 1 per mandare alla fine dello stat che crea lo while se la condizione è falsa 
			code.emitLabel(succ);		
			stat(lnext);		
			code.emit(OpCode.GOto, body);
    			break;
    		case Tag.DO:	// non credo serva fare nulla di speciale
    			move();
    			statlist();
    			break;
    		case Tag.PRINT:		// forse necessario differenziare tra ID e numero
    			move();
    			exprlist();
    			code.emit(OpCode.invokestatic, 1);	// invokestatic a 1 fa output, altrimenti input
    			break;
	// ... completare ...
            case Tag.READ:
                move();
                if (look.tag==Tag.ID) {
                    int read_id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (read_id_addr == -1) {
                        read_id_addr = count;
                        st.insert(((Word)look).lexeme, count++);
                    }                    
                    move();
                    code.emit(OpCode.invokestatic, 0);
                    code.emit(OpCode.istore, read_id_addr);   
                }
                else
                    error("Error in grammar (stat) after read with " + look);
                break;
	// ... completare ...
        }
    }
    
    private void elseopt(int lnext) {
    	if(look.tag == '('){
	move();
	if(look.tag == Tag.ELSE){
	move();
	stat(lnext);
	}else	error("Error in elseopt");
	match(')');
	}
    }
    
    private void bexpr(int tru, int fals) {
    	if(look.tag == '(') {
	move();
	bexprp(tru, fals);
	match(')');
    	}else error("Error in bexpr");
    }
    
    private void bexprp(int tru, int fals) {
    	if(look.tag == Tag.RELOP) {
    		Token temp = look;
    		move();
		expr();
		expr();
    		switch(((Word)temp).lexeme) {	// a sto giro sarà necessario specificare l'azione (inversa perché ijvm) in base al lessema
    			case "==":
    				code.emit(OpCode.if_icmpne, lnext);	// se vero salta il codice per la condizione originale vera (quindi vai al falso)
    				break;
    			case "<>":
    				code.emit(OpCode.if_icmpeq, lnext);
    				break;
    			case "<=":
    				code.emit(OpCode.if_icmpgt, lnext);
				break;
    			case ">=":
    				code.emit(OpCode.if_icmplt, lnext);
    				break;
    			case "<":
    				code.emit(OpCode.if_icmpge, lnext);
    				break;
    			case ">":
    				code.emit(OpCode.if_icmple, lnext);
    		}
    	}else error("Error in bexprp");
    }

    private void expr() {
    	switch (look.tag) {
    		case Tag.NUM:
                code.emit(OpCode.ldc, ((NumberTok)look).lexeme);
                operands++;
    			move();
    			break;
    		case Tag.ID:
				int read_id_addr = st.lookupAddress(((Word)look).lexeme);
				if(read_id_addr == -1)	error("Variable does not exist");
    			code.emit(OpCode.iload, read_id_addr);
    			operands++;
    			move();
    			break;
    		case '(':
    			move();
    			exprp();
    			match(')');
    			break;
    		default:
    			error("Missing operand");
    	}
    }

    private void exprp() {	// aggiunta la variabile operands che conta il numero di operandi presenti in somme e prodotti
		switch (look.tag) {
			case '+':
				move();
				exprlist();
				if(operands == 1)	code.emit(OpCode.ldc, 0);	// se è presente un solo operando pusho 0 sullo stack per non sommare il numero a qualcosa di inaspettato
                code.emit(OpCode.iadd);
                operands = 0;
				break;
			case '-':
				move();
				expr();
				expr();
                code.emit(OpCode.isub);
				break;
			case '*':
				move();
				exprlist();
				if(operands == 1)	code.emit(OpCode.ldc, 1);	// se è presente un solo operando pusho 1 sullo stack per non moltiplicare il numero per qualcosa di inaspettato
                code.emit(OpCode.imul);
                operands = 0;
				break;
			case '/':
				move();
				expr();
				expr();
                code.emit(OpCode.idiv);
		}
    }

    private void exprlist() {
    	expr();
    	exprlistp();
    }

    private void exprlistp() {
    	switch (look.tag) {		// non necessario muovermi perché qualsiasi cosa io abbia la gestisco in expr
    		case Tag.NUM:
    		case Tag.ID:
    		case '(':
				expr();
				exprlistp();
		}
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Input.pas"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    } 
}
