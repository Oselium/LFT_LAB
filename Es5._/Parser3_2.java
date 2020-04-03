import java.io.*;
public class Parser3_2 {
  private Lexer lex;
  private BufferedReader pbr;
  private Token look;

  public Parser3_2(Lexer l, BufferedReader br) {
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

  public void prog(){
      stat();
      match(Tag.EOF);
  }

  private void statlist() {
      stat();
      statlistp();
  }

  private void statlistp() {
    if(look.tag != ')') {
    stat();
    statlistp();
    }
  }

  private void statp() {
    switch(look.tag) {
      case '=':
        move();
        match(Tag.ID);
        expr();
        break;
      case Tag.COND:
        move();
        bexpr();
        stat();
        elseopt();
        break;
      case Tag.WHILE:
        move();
        bexpr();
        stat();
        break;
      case Tag.DO:
        move();
        statlist();
        break;
      case Tag.PRINT:
        move();
        exprlist();
        break;
      case Tag.READ:
        move();
        match(Tag.ID);

    }
  }

  private void stat() {
      if(look.tag == '(') {
        move();
        statp();
        match(')');
      } else  error("Error in stat");
    }

  private void elseopt(){
    if(look.tag == '('){
      move();
      match(Tag.ELSE);
      stat();
      match(')');
    }
  }

  private void bexpr(){
    if(look.tag == '('){
      move();
      bexprp();
      match(')');
    }
  }

  private void bexprp(){
    switch(look.tag){
      case Tag.RELOP:
        move();
        expr();
        expr();
    }
  }

  private void expr() {
    switch(look.tag){
      case Tag.NUM:
        move();
        break;
      case Tag.ID:
        move();
        break;
      case '(':
        move();
        exprp();
        match(')');
        break;
      default:
        error("Error in expr");
    }
  }

  private void exprp() {
    switch(look.tag){
      case '+':
        move();
        exprlist();
        break;
      case '-':
        move();
        expr();
        expr();
        break;
      case '*':
        move();
        exprlist();
        break;
      case '/':
        move();
        expr();
        expr();
        break;
      default:
        error("Error in exprp");
    }
  }

  private void exprlist(){
    expr();
    exprlistp();
  }

  private void exprlistp(){
    if(look.tag == Tag.NUM || look.tag == Tag.ID){
      expr();
      exprlistp();
    }else if(look.tag == '('){
      move();
      expr();
      exprlistp();
    }else if(look.tag != ')'){
      //error("Error in exprlistp");
    }
  }

  public static void main(String[] args) {
    Lexer lex = new Lexer();
    String path = "/home/oselium/Documents/Secondo Anno/LFT/LAB/Esercizi.3_/Es3_2.txt"; // il percorso del file da leggere
    try {
    BufferedReader br = new BufferedReader(new FileReader(path));
    Parser3_2 parser = new Parser3_2(lex, br);
    parser.prog();
    System.out.println("Input OK");
    br.close();
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}
