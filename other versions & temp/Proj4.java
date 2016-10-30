import java.io.*;
import java.util.Scanner;

/*
* @author Michael Hearn ( mthearn )
*/
public class Proj4 {

	public final static int M = 31991;
	//public static final int M = 32768;
	public static final int NUMER_OF_CHARS = 6;
	public static final int MAX_KIDS = 35;
	public static final double GOLDEN_RATIO = ( 1 + ( Math.sqrt( 5 ) )) / 2;
	public static final int UPPER_A = 65;
	public static final int UPPER_Z = 90;


	public static String[][] myHash = new String[ M ][ MAX_KIDS ];
	//public static int N = 25253; // temporary count this later


	public static int collisions = 0;
	public static int spellProbes = 0;
	public static int wordCount = 0;
	public static int spellWordCount = 0;
	public static int misSpelledWords = 0;


	/*
	* starting point of the program
	* initializes our data structures 
	* and calls our other functions to fill those structures
	* @param args, string array of arguments
	*/
	public static void main(String[] args) throws FileNotFoundException {

		Scanner console = new Scanner( System.in );
		// read input file
		readInHash( console );
		readInText( console );
		report();
		//printHash();

		console.close();

	}

	/*
	* reads in the input
	*/
	public static void readInText( Scanner console ) throws FileNotFoundException {

		Scanner textReader = new Scanner(new File( console.next() ) );

		Scanner line;
		String temp;
		while ( textReader.hasNextLine() ) {
			line = new Scanner ( textReader.nextLine() );

			while ( line.hasNext() ) {
				temp = line.next();
				spellWordCount++;				// chang this to diff location later
				//temp = prepWord( temp );

				String[] diffWords = temp.split( "[^a-zA-Z0-9']");
				int count = 0;
				while ( count < diffWords.length && diffWords[count] != null ) {
					
					temp = diffWords[count];
					//System.out.println("--------------word is after delimmit: " + temp );
					count++;
					if ( !temp.equals("") && findWord( temp ) == false ) {
						misSpelledWords++;
					}
				}
				

				
				
	
			} // done with this word
			line.close();
		} // end of line 
		textReader.close();
	}

	public static String prepWord( String theWord ) {
		if ( theWord.length() == 0 ) return "-----not sure what happened here";

		if (theWord.charAt(0) >= 33 && theWord.charAt(0) <= 47 || ( theWord.charAt(0) >= 58 && theWord.charAt(0) <= 64 )) {
			//System.out.print( "-------------test-------- word was " + theWord );
			theWord = theWord.substring(1);
			//System.out.println( "-------------test-------- word is now " + theWord );
			theWord = prepWord( theWord );
		}
		if ( theWord.charAt( theWord.length() - 1 ) >= 33 && theWord.charAt( theWord.length() - 1 ) <= 47 
			|| ( theWord.charAt( theWord.length() - 1 ) >= 58 && theWord.charAt( theWord.length() - 1 ) <= 64 )) {
			//System.out.print( "-------------test-------- word was " + theWord );
			theWord = theWord.substring(0, theWord.length() - 1);
			//System.out.println( "-------------test-------- word is now " + theWord );
			
			theWord = prepWord( theWord );
		}
		return theWord;
	}

	public static boolean findWord( String theWord ) {

		if ( theWord.length() == 0 ) return false;

		int idx = getidx( theWord, NUMER_OF_CHARS );
		int k = 0;
		
		while ( myHash[ idx ][ k ] != null && (!myHash[ idx ][ k ].equals( theWord )) ) {
				
			spellProbes++;
			k++;

			if ( k >= MAX_KIDS || myHash[ idx ][ k ] == null) {

				System.out.println( theWord );

				if ( theWord.charAt( 0 ) >= UPPER_A && theWord.charAt( 0 ) <= UPPER_Z ) { 
					
					return findWord( theWord.toLowerCase() );

				}
				if ( theWord.endsWith("\'s") ) {
					
					return findWord( theWord.substring(0, ( theWord.length() - 2 ) ) );
				}

				if ( theWord.endsWith("s") ) {
					boolean checker = false;
					checker = findWord( theWord.substring(0, ( theWord.length() - 1 ) ) );
					// if first search failed try again
					if ( !checker && theWord.endsWith("es") ) {
						checker = findWord( theWord.substring(0, ( theWord.length() - 2 ) ) );
					}
					return checker;
				}

				if ( theWord.endsWith("ed") ) {
					boolean checker = false;
					checker = findWord( theWord.substring(0, ( theWord.length() - 2 ) ) );
					// if first search failed try again dropping only d
					if ( !checker ) {
						checker = findWord( theWord.substring(0, ( theWord.length() - 1 ) ) );
					}
					return checker;
				}

				if ( theWord.endsWith("er") ) {
					boolean checker = false;
					checker = findWord( theWord.substring(0, ( theWord.length() - 2 ) ) );
					// if first search failed try again dropping only r
					if ( !checker ) {
						checker = findWord( theWord.substring(0, ( theWord.length() - 1 ) ) );
					}
					return checker;
				}

				if ( theWord.endsWith("ing") ) {
					boolean checker = false;
					checker = findWord( theWord.substring(0, ( theWord.length() - 3 ) ) );
					// if first search failed try again replacing ing with e
					if ( !checker ) {
						theWord = theWord.substring( 0, theWord.length() - 3 );
						checker = findWord( theWord.concat("e") );
					}
					return checker;
				}

				if ( theWord.endsWith("ly") ) {
					return findWord( theWord.substring(0, ( theWord.length() - 2 ) ) );
				}


				//System.out.println( " word not found " + theWord );
				return false;
			}

		}

		return true;
	}

	/*
	* reads in the input
	* also fills in the data structures with that data
	*/
	public static void readInHash( Scanner console ) throws FileNotFoundException {

		Scanner reader = new Scanner(new File( console.next() ) );

		String temp;
		while ( reader.hasNextLine() ) {
			wordCount++;
			temp = reader.nextLine();
			int idx = getidx( temp, NUMER_OF_CHARS );
			//System.out.println( " testline : idx is : " + idx );
			int k = 0 ;

			while ( myHash[ idx ][ k ] != null ) {
				//place collision resolve here
				collisions++;
				k++;
			}
			myHash[ idx ][ k ] = temp;
		}
		reader.close();
	}

	/*
	* this function will resolve collisions and assign a new index
	* @param idx, current invalid index
	* @return new index to be used
	*/
	public static int getResolvedIdx( int idx ) {

		return idx = ( idx + 4 ) % M;
	}

	/*
	* this is the hash function that assigns an integer to a string
	* which will represent its index into the array of strings
	* a polynomial hashing function that takes order into account
	* @param temp, the string used to create the index
	* @return idx, index of the string we are looking for
	*/
	public static int getidx( String temp, int myCount ) {
		// i can write this loop better
		long idx = 0;
		int loopCount = Math.min( myCount, temp.length() );

		for ( int i = 0; i < loopCount; i++ ) {
			char tempChar = temp.charAt( i );

			idx += Math.pow( tempChar, ( loopCount - i ) );

		}

		//compress idx
		double fk_inverted = idx * ( 1 / GOLDEN_RATIO );

		int crazyNum = ( int )( M * ( (fk_inverted) - (long)(fk_inverted)) );
		//System.out.println("eerror?: crazyNum is : " + crazyNum );
		//System.out.println( " idx is: " + idx + " fk_inverted is:  " + fk_inverted + ", crazyNum is : " + crazyNum + " - ");

														// for testing
														
														if ( crazyNum >= M ) {
															System.out.println( " super high: " + temp + ",  " + idx + ", minCOunt/loop = " + loopCount + " mycount/param was : " + myCount );
															for ( int i = 0; i < loopCount; i++ ) {
																System.out.println( temp.charAt( i ) +" ^ " + ( loopCount - i ) + "  = " + Math.pow( temp.charAt( i ), ( loopCount - i ) ));

															}
															System.exit(0);
														}
		return crazyNum;
	}

	/*
	* prints the hash table for testing
	*/
	public static void printHash() {
		int count = 0;

		for ( int i = 0; i < M; i++ ) {
			int k = 0;
			//if ( myHash[ count ][ 0 ] == null ) continue;
			System.out.print( " line " + i + " " );
			//System.out.println( myHash[ i ][ k ]  );
			while ( myHash[ i ][ k ] != null ) {
				System.out.print( myHash[ i ][ k ] + ", "  );
				k++;
			}
			System.out.println();
		}
	}

	public static void report() {
		System.out.println( "Constructing dictionary had this many collisions: " + collisions );
		System.out.println( "Amount of words in the dictionary: " + wordCount );
		System.out.println( "Amount of words to check: " +  spellWordCount );
		System.out.println( "Amount of misspelled words: " +  misSpelledWords );

		System.out.println( "checking spell file had this many probes: " + spellProbes );
		System.out.println( "average probes per word: " + ( (double)spellProbes / spellWordCount) );

		System.out.println( "-----------------------------------" );
		System.out.println();
	}
}
