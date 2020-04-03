public class Es1_9
{
  public static boolean scan(String s, String t)
  {
  int state = 0;
  int i = 0;
  if(s.length() != t.length()) state = -1;
  while (state >= 0 && i < s.length()) {
    final char ch = s.charAt(i);
    final char dh = t.charAt(i);
      switch (state) {
      case 0:
        if (ch == dh)
        state = 0;
        else if (ch != dh)
        state = 1;
        else
        state = -1;
        break;
      case 1:
        if (ch == dh)
        state = 1;
        else
        state = -1;
        break;
    }
    i++;
  }
  if(state == 0 || state == 1)
  return true;
  else return false;
  }

  public static void main(String[] args){
    System.out.println(scan(args[0],args[1]) ? "OK" : "NOPE");
  }
}
