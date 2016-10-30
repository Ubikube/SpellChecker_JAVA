import java.io.*;
import java.util.Scanner;

/*
* a spellchecker program
* that will begin by filling an array
* with strings- this will be our dictionary
* this will come from the file dict.txt ( line 114 if you wish to change to another dictionary )
*
* afterwards will read a file ( SysIn ) and check it 
* against the dictionary for misspelled words
* finally it will report statistics from the program
*
* run with java Proj4 < SysIn.txt > nameofOutputFile.txt
* inside Sysin.txt ( or whatever file you put here ) should be a single line that has the name of the file you wish to check
* I am including my "big.txt" file that was posted on forum by someone
* that I have been using as a tester
*
* Demonstrated data structures: singly linked list and hash tables
*
* @author Michael Hearn ( mthearn )
*/
public class SpellChecker {
	
	/* used by polynomial hash function to determine how many
	*  letters to count towards hashing
	*  ex: with 8, ABCDEFGH3 == ABCDEFGH2 */
	public static final int NUMER_OF_CHARS = 8;
	/* represents the golden ratio */
	public static final double GOLDEN_RATIO = ( 1 + ( Math.sqrt( 5 ) )) / 2;
	/* represents ascii value of capital A */
	public static final int UPPER_A = 65;
	/* represents ascii value of capital Z */
	public static final int UPPER_Z = 90;
	/* size of the hash table, roughly 25% larger than dictionary */
	public final static int hashSize = 31991;
	/* represents a lookup operation that did not find the word */
	public static final int NOT_FOUND = -42;

	/* this will be my hash table */
	public static PointerNode[] myHash = new PointerNode[ hashSize ];
	/* used for testing, represents number of collisions when building hash table */
	public static int collisions = 0;
	/* number of probes when spell checking */
	public static int spellProbes = 0;
	/* number of words in dictionary */
	public static int wordCount = 0;
	/* number of words in text file to spellcheck */
	public static int spellWordCount = 0;
	/* number of misspelled words */
	public static int misSpelledWords = 0;
	/* represents the number of lookup operations */
	public static int lookupOperation = 0;

	/*
	* nested class used to hold Strings of dictionary
	* will be used to create a linked list at every index 
	* in the hash table that has collsions
	*/
	public static class Node {
		/* the string from dictionary */
		public String value;
		/* pointer to next node on linked list */
		public Node next;

		/* construct node with string, and a null value for next */
		public Node( String val ) {
			value = val;
			next = null;
		}
	}

	/*
	* nested class simply a pointer
	* as this only holds a pointer
	* it is smaller than the Node class
	*/
	public static class PointerNode {
		/* pointer to next node on linked list */
		public Node next;

		// simple pointer init with a null value for next 
		public PointerNode( ) {
			next = null;
		}
	}

	/*
	* starting point of the program
	* initializes our data structures 
	* and calls our other functions to fill those structures
	* @param args, string array of arguments
	*/
	public static void main(String[] args) throws FileNotFoundException, IOException {

		Scanner console = new Scanner( System.in );
		// initialize my array with pointers basically
		for ( int i = 0; i < hashSize; i++ ) {
			myHash[ i ] = new PointerNode( );
		}

		fillHash();
		spellCheck( console );
		report();

		console.close();

	}

	/*
	* reads in the input and fills the hash table
	* @param console, reads standard input
	*/
	public static void fillHash() throws FileNotFoundException {

		Scanner reader = new Scanner(new File( "dict.txt" ));
		String temp;

		// reads each line and puts the word into the hashtable
		while ( reader.hasNextLine() ) {
			wordCount++;
			temp = reader.nextLine();
			// get the index from hashfunction
			int idx = getidx( temp, NUMER_OF_CHARS );
			Node current = myHash[ idx ].next;
			// if nothing at that idx put word there
			if ( current == null ) {
				myHash[ idx ].next = new Node( temp );
			// otherwise put it into the linked list
			} else {
				// iterates until end of list or until word is found
				while ( current.next != null ) {
					if ( current.next.value.equals( temp ) ) {
						break;
					}
					current = current.next;
					collisions++;
				}
				// but only addes if we are at the end of the list
				if ( current.next == null ) {
					current.next = new Node( temp );
				}
			}
		}
		reader.close();
	}

	/*
	* this performs the spell check function
	* scans each line of text, splits it, and looks for it in the hashtable
	* @param console, reads standard input
	*/
	public static void spellCheck( Scanner console ) throws FileNotFoundException {

		Scanner textReader = new Scanner(new File( console.next() ) );
		Scanner line;
		String temp;

		while ( textReader.hasNextLine() ) {
			line = new Scanner ( textReader.nextLine() );

			// will process each token and split using non accepted chars as delimiters
			while ( line.hasNext() ) {
				temp = line.next();
				String[] diffWords = temp.split( "[^a-zA-Z0-9']+");
				int count = 0;
				// goes through array of split words and find those that arent ""
				// which can happen when splitting
				while ( count < diffWords.length && diffWords[count] != null ) {
					temp = diffWords[count];
					if ( !temp.equals("")) spellWordCount++; // counts total words
					count++; // index of items in diffWords
					if ( !temp.equals("") && findWord( temp ) == NOT_FOUND ) {
						System.out.println( temp );
						misSpelledWords++;  // counts mispelled words
					}
				}
			} // done with this word
			line.close();
		} // end of line 
		textReader.close();
	}

	/*
	* this is the hash function that assigns an integer to a string
	* which will represent its index into the array of strings
	* uses a polynomial hashing function that takes order into account
	* @param temp, the string used to create the index
	* @param myCount, int represents how many characters into string
	* 					to read to find the index
	* @return idx, index of the string we are looking for
	*/
	public static int getidx( String temp, int myCount ) {
		long idx = 0;
		int loopCount = Math.min( myCount, temp.length() );

		for ( int i = 0; i < loopCount; i++ ) {
			char tempChar = temp.charAt( i );
			idx += ( tempChar * (Math.pow( 10, ( loopCount - i ))) );
		}
		//compress idx with golden ration method
		double fk_inverted = idx * ( 1 / GOLDEN_RATIO );
		int index = ( int )( hashSize * ( (fk_inverted) - (long)(fk_inverted)) );
		
		return index;
	}

	/*
	* prints out the statistics as per instructions
	*/
	public static void report() {
		System.out.println( "-------------------------------------------------------------------------" );
		System.out.println( "Report:                         									    ;" );
		System.out.println( "-------------------------------------------------------------------------" );

		String one = new String( "(1) Number of words in the dictionary:" );
		String two = new String( "(2) Number of words in the text to be spell-checked:" );
		String three = new String( "(3) Number of misspelled words in the text:" );
		String four = new String( "(4) Number of probes during the checking phase:" );
		String five = new String( "(5) Average probes per word:" );
		String six = new String( "(6) Average probes per lookup Operation:" );

		System.out.printf( "%-60s %-11d;\n", one, wordCount );
		System.out.printf( "%-60s %-11d;\n", two, spellWordCount );
		System.out.printf( "%-60s %-11d;\n", three, misSpelledWords );
		System.out.printf( "%-60s %-11d;\n", four, spellProbes );
		System.out.println( "-------------------------------------------------------------------------" );
		System.out.printf( "%-60s %-11f;\n", five, ( (double)spellProbes / spellWordCount ) );
		System.out.printf( "%-60s %-11f;\n", six, ( (double)spellProbes / lookupOperation) );
		System.out.println( "-------------------------------------------------------------------------" );
		System.out.println();
	}

	/*
	* looks for the word in the hash table
	* once the index is accessed, will iterate 
	* from word to word until the word is found
	* if it is not, the word will be trimmed as per rules
	* and sought again
	* @param theWord, the word we are looking to find
	* @return idx of the word in the hash table
	* 			or NOT_FOUND( -42 ) if the word is not found
	*/
	public static int findWord( String theWord ) {
		lookupOperation++;
		// base case for security
		if ( theWord.length() == 0 ) return NOT_FOUND;
		// search index of the word where it should be
		int idx = getidx( theWord, NUMER_OF_CHARS );
		//System.out.println( theWord + ", looking for at idx " + idx );
		PointerNode pointerCurrent = myHash[ idx ];
		// this is done to prime the loop in case 1st item is not initialized yet
		if ( pointerCurrent.next == null ) {
			pointerCurrent.next = new Node( null );
		}
		Node current = pointerCurrent.next;
		
		// as long as it doesnt match value at this idx iterate thru list here
		// FYI my list is simply a series of linked nodes
		// did it this way to save space, doesnt require as much memory for the
		// entire list object with lots of superfluous data
		while ( !theWord.equals( current.value ) ) {
			// above loop ( by definition ) means a comparison to an actual word
			// so we increment our spell probes
			spellProbes++;
			current = current.next;

			// if we hit end of linked list, we havent found the word
			// so we trim it per instructions according to series of if statements
			// and then recursively call again with new trimmed word
			if ( current == null ) {

				// if first letter is capital
				if ( theWord.charAt( 0 ) >= UPPER_A && theWord.charAt( 0 ) <= UPPER_Z ) { 
					return findWord( theWord.toLowerCase() );
				}
				if ( theWord.endsWith("\'s") ) {	
					return findWord( theWord.substring(0, ( theWord.length() - 2 ) ) );
				}
				if ( theWord.endsWith("s") ) {
					int checker = NOT_FOUND;
					checker = findWord( theWord.substring(0, ( theWord.length() - 1 ) ) );
					// if first search failed try again
					if ( checker == NOT_FOUND && theWord.endsWith("es") ) {
						checker = findWord( theWord.substring(0, ( theWord.length() - 2 ) ) );
					}
					return checker;
				}
				if ( theWord.endsWith("ed") ) {
					int checker = NOT_FOUND;
					checker = findWord( theWord.substring(0, ( theWord.length() - 2 ) ) );
					// if first search failed try again dropping only d
					if ( checker == NOT_FOUND ) {
						checker = findWord( theWord.substring(0, ( theWord.length() - 1 ) ) );
					}
					return checker;
				}
				if ( theWord.endsWith("er") ) {
					int checker = NOT_FOUND;
					checker = findWord( theWord.substring(0, ( theWord.length() - 2 ) ) );
					// if first search failed try again dropping only r
					if ( checker == NOT_FOUND ) {
						checker = findWord( theWord.substring(0, ( theWord.length() - 1 ) ) );
					}
					return checker;
				}
				if ( theWord.endsWith("ing") ) {
					int checker = NOT_FOUND;
					checker = findWord( theWord.substring(0, ( theWord.length() - 3 ) ) );
					// if first search failed try again replacing ing with e
					if ( checker == NOT_FOUND ) {
						theWord = theWord.substring( 0, theWord.length() - 3 );
						checker = findWord( theWord.concat("e") );
					}
					return checker;
				}
				if ( theWord.endsWith("ly") ) {
					return findWord( theWord.substring(0, ( theWord.length() - 2 ) ) );
				}
				// and if the word is simply not in our list return NOT_FOUND
				return NOT_FOUND;
			} // if current == null ( if weve reached end or empty array idx )
		}// end while loop to iterate through linked list if idx != null
		// reaching here means we found our word in the while loop
		return idx;
	}
}
