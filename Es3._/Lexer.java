import java.io.*;
import java.util.*;
public class Lexer {
public static int line = 1;
private char peek = ' ';

private void readch(BufferedReader br) {
  try {
  peek = (char) br.read();
  } catch (IOException exc) {
  peek = (char) -1; // ERROR
  }
}

public Token lexical_scan(BufferedReader br) {
  while (peek == ' '|| peek == '\t' || peek == '\n' || peek == '\r') {
  if (peek == '\n') line++;
  readch(br);
  }
  switch (peek) {
  case '!':
  peek = ' ';
  return Token.not;
  case '(':
  peek = ' ';
  return Token.lpt;
  case ')':
  peek = ' ';
  return Token.rpt;
  case '{':
  peek = ' ';
  return Token.lpg;
  case '}':
  peek = ' ';
  return Token.rpg;
  case '+':
  peek = ' ';
  return Token.plus;
  case '-':
  peek = ' ';
  return Token.minus;
  case '*':
  peek = ' ';
  return Token.mult;
  case '/':
  peek = ' ';
  return Token.div;
  case ';':
  peek = ' ';
  return Token.semicolon;
  //---------//
  case '&':
  readch(br);
  if (peek == '&') {
  peek = ' ';
  return Word.and;
  } else {
  System.err.println("Erroneous character"
  + " after & : " + peek );
  return null;
  }
  case '|':
  readch(br);
  if (peek == '|') {
  peek = ' ';
  return Word.or;
  } else {
  System.err.println("Erroneous character"
  + " after | : " + peek );
  return null;
  }
  case '<':
  readch(br);
  if (peek == '='){
    peek = ' ';
    return Word.le;
  }else if(peek == '>') {
    peek = ' ';
    return Word.ne;
  }else{
  return Word.lt;
  }
  case '>':
  readch(br);
  if (peek == '='){
    peek = ' ';
    return Word.ge;
  }else{
  return Word.gt;
  }
  case '=':
  readch(br);
  if (peek == '='){
    peek = ' ';
    return Word.eq;
  }else{
  return Token.assign;
  }
  case (char)-1:
  return new Token(Tag.EOF);
  default:
  if (Character.isLetter(peek)) {
  // ... gestire il caso degli identificatori e delle parole chiave //
    String tok = "";
    while(Character.isLetter(peek) || Character.isDigit(peek)){
      tok = tok + peek;
      readch(br);
    }
    switch(tok){
      case "cond":
      //peek = ' ';
      return Word.cond;
      case "when":
      //peek = ' ';
      return Word.when;
      case "then":
      //peek = ' ';
      return Word.then;
      case "else":
      //peek = ' ';
      return Word.elsetok;
      case "while":
      //peek = ' ';
      return Word.whiletok;
      case "do":
      //peek = ' ';
      return Word.dotok;
      case "seq":
      //peek = ' ';
      return Word.seq;
      case "print":
      //peek = ' ';
      return Word.print;
      case "read":
      //peek = ' ';
      return Word.read;
      default:
      //peek = ' ';
      return new Word(257,tok);
    }
  } else if (Character.isDigit(peek)) {
  // ... gestire il caso dei numeri ... //
  String num = "";
  while(Character.isDigit(peek)){
    num = num + peek;
    readch(br);
  }
  return new NumberTok(Integer.parseInt(num));
  } else {
    System.err.println("Erroneous character: "
    + peek );
    return null;
  }
}
}
//--------------//
public static void main(String[] args) {
  Lexer lex = new Lexer();
  String path = "/home/oselium/Documents/Secondo Anno/LFT/LAB/Esercizi.3_/text.txt"; // il percorso del file da leggere
  try {
    BufferedReader br = new BufferedReader(new FileReader(path));
    Token tok;
  do {
    tok = lex.lexical_scan(br);
    System.out.println("Scan: " + tok);
  } while (tok.tag != Tag.EOF);
    br.close();
  } catch (IOException e) {e.printStackTrace();}
  }
}
