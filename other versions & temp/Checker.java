import java.io.*;
import java.util.Scanner;

public class Checker {
  public static void main ( String args[] ) {
    int count = 0;
    Scanner console = new Scanner( System.in );
    while ( console.hasNext() ) {
      console.next();
      count++;
    }
    System.out.println( count );

  }
}