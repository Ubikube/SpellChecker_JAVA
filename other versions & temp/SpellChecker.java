import java.io.*;
import java.util.Scanner;

/*
* @author Michael Hearn ( mthearn )
*/
public class SpellChecker {

	public final static int M = 31991;
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
	* also fills in the data structures with that data
	*/
	public static void readInHash( Scanner console ) throws FileNotFoundException {

		Scanner reader = new Scanner(new File( console.next() ) );

		String temp;
		while ( reader.hasNextLine() ) {
			wordCount++;
			temp = reader.nextLine();
			int idx = getidx( temp, NUMER_OF_CHARS );
			int k = 0 ;

			//iterate through array until we find an open/null spot
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
	* reads in the input
	*/
	public static void readInText( Scanner console ) throws FileNotFoundException {
		// reads the file we wish to spellcheck from 2nd line of Sysin.txt
		Scanner textReader = new Scanner(new File( console.next() ) );

		Scanner line;
		String temp;
		while ( textReader.hasNextLine() ) {
			line = new Scanner ( textReader.nextLine() );

			while ( line.hasNext() ) {
				temp = line.next();
				spellWordCount++;
				//temp = prepWord( temp );

				String[] diffWords = temp.split( "[^a-zA-Z0-9']");
				int count = 0;
				while ( count < diffWords.length && diffWords[count] != null ) {
					
					temp = diffWords[count];
					//System.out.println("--------------word is after delimmit: " + temp );
					count++;
					if ( !temp.equals("") && findWord( temp ) == false ) {
						System.out.println( "word not found: " + temp );
						misSpelledWords++;
					}
				}
			} // done with this word
			line.close();
		} // end of line 
		textReader.close();
	}

	/*
	* looks for the word in the array
	* if it is not found where it should be it will break the word down
	* (according to the rules specified in the project )
	* and search again recursively
	* example- downloading is not found so we will look for download and then downloade
	* if the word is never found we return false, true otherwise
	* @param theWord, a string representing the word we are looking to find
	* @return boolean representing whether we ever found the word in our list
	*/
	public static boolean findWord( String theWord ) {

		if ( theWord.length() == 0 ) return false;

		int idx = getidx( theWord, NUMER_OF_CHARS );
		int k = 0;

		// if I have either reached the end of the list OR I havent found my word at that index yet
		// continue to break the word down and search for it again recursively
		while ( myHash[ idx ][ k ] != null && (!myHash[ idx ][ k ].equals( theWord )) ) {
				
			spellProbes++;
			k++;

			if ( k >= MAX_KIDS || myHash[ idx ][ k ] == null) {

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
				return false;
			}
		}
		return true;
	}

	/*
	* this is the hash function that assigns an integer to a string
	* which will represent its index into the array of strings
	* a polynomial hashing function that takes order into account
	* @param temp, the string used to create the index
	* @return idx, index of the string we are looking for
	*/
	public static int getidx( String temp, int myCount ) {
		long idx = 0;
		int loopCount = Math.min( myCount, temp.length() );

		for ( int i = 0; i < loopCount; i++ ) {
			char tempChar = temp.charAt( i );

			idx += Math.pow( tempChar, ( loopCount - i ) );
		}

		//compress idx
		double fk_inverted = idx * ( 1 / GOLDEN_RATIO );

		int crazyNum = ( int )( M * ( (fk_inverted) - (long)(fk_inverted)) );

		return crazyNum;
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
