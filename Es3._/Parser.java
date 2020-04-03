import java.io.*;
public class Parser {
  private Lexer lex;
  private BufferedReader pbr;
  private Token look;

  public Parser(Lexer l, BufferedReader br) {
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
    if(look.tag == Tag.NUM || look.tag == '('){
      expr();
      match(Tag.EOF);
    }else error("Error in start");
  }

  private void expr() {
    if(look.tag == Tag.EOF){
      error("Error in expr");
    }else{
      term();
      exprp();
    }
  }

  private void exprp() {
    switch(look.tag){
      case '+':
        move();
        term();
        exprp();
        break;
      case '-':
        move();
        term();
        exprp();
	break;
      case Tag.EOF:
      case ')':
        break;
      default:
        error("Error in exprp");
    }
  }

  private void term() {
    if(look.tag == '(' || look.tag == Tag.NUM){
      fact();
      termp();
    }else{
      error("Error in term");
    }
  }

  private void termp() {
    switch(look.tag){
      case '*':
        move();
        fact();
        termp();
        break;
      case '/':
        move();
        fact();
        termp();
      case Tag.EOF:
      case ')':
      case '+':
      case '-':
        break;
      default:
        error("Error in termp");
    }
  }

  private void fact() {
    switch(look.tag){
      case '(':
        move();
        expr();
        match(')');
        break;
      case Tag.NUM:
        move();
        break;
      default:
        error("Error in fact");
    }
  }

  public static void main(String[] args) {
    Lexer lex = new Lexer();
    String path = "/home/oselium/Documents/Secondo Anno/LFT/LAB/Esercizi.3_/Es3_1.txt"; // il percorso del file da leggere
    try {
    BufferedReader br = new BufferedReader(new FileReader(path));
    Parser parser = new Parser(lex, br);
    parser.start();
    System.out.println("Input OK");
    br.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}
