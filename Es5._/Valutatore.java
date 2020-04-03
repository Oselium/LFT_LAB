import java.io.*;

public class Valutatore {
  private Lexer lex;
  private BufferedReader pbr;
  private Token look;
  public Valutatore(Lexer l, BufferedReader br) {
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
    }else error("syntax error");
  }

  public void start() {
    	if(look.tag != Tag.NUM && look.tag != '(') error("Error in start");
	int expr_val;
    	expr_val = expr();
	match(Tag.EOF);
        System.out.println("Il risultato è -> " + expr_val);
    }

    private int expr() {
	if(look.tag != Tag.NUM && look.tag != '(')	error("Error in expr");
	int term_val, exprp_val;
    	term_val = term();
	exprp_val = exprp(term_val);
	return exprp_val;
    }

    private int exprp(int exprp_i) {
	int term_val, exprp_val = exprp_i;
	switch (look.tag) {
	 case '+':
          move();
          term_val = term();
          exprp_val = exprp(exprp_i + term_val);
         break;
	 case '-':
	  move();
	  term_val = term();
	  exprp_val = exprp(exprp_i - term_val);
	 case ')':
	 case Tag.EOF:
	  exprp_val = exprp_i;
	 }
	return exprp_val;
	}

    private int term() {
    	if(look.tag != Tag.NUM && look.tag != '(')	error("Error in term");
    	int fact_val, termp_val;
        fact_val = fact();
        termp_val = termp(fact_val);
        return termp_val;
    }

    private int termp(int termp_i) {
    	int fact_val, termp_val = termp_i;
    	switch (look.tag) {
	case '*':
		move();
		fact_val = fact();
		termp_val = termp(termp_i * fact_val);
		break;
	case '/':
		move();
		fact_val = fact();
		termp_val = termp(termp_i / fact_val);
	case ')':
	case '+':
	case '-':
	case Tag.EOF:
	 termp_val = termp_i;
	 break;
	default:
		error("Error in termp");
	}
	return termp_val;
    }

    private int fact() {
    	int expr_val, fact_val, val = 0;
    	switch (look.tag) {
    		case '(':
    			move();
    			val = expr_val = expr();
    			match(')');
    			break;
    		case Tag.NUM:
    			val = fact_val = ((NumberTok)look).lexeme;
    			move();
    			break;
    		default:
    			error("Error in fact");
    	}
    	return val;
    }

  public static void main(String[] args) {
      Lexer lex = new Lexer();
      String path = "/home/oselium/Documents/Secondo Anno/LFT/LAB/Esercizi.4_/text.txt"; // il percorso del file da leggere
    try {
      BufferedReader br = new BufferedReader(new FileReader(path));
      Valutatore valutatore = new Valutatore(lex, br);
      valutatore.start();
    br.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}
