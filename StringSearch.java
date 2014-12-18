import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StringSearch {

	/**
	 * Find all instances of pattern in text using Boyer-Moore algorithm.
	 * Use buildBoyerMooreCharTable to build your reference table.
	 * 
	 * @param pattern
	 * The String to find
	 * @param test
	 * The String to look through
	 * @return
	 * A List of starting indices where pattern was found in text
	 */
	public static List<Integer> boyerMoore(String pattern, String text) {
		int[] map = buildBoyerMooreCharTable(pattern);
		ArrayList<Integer> a = new ArrayList<Integer>();
		if(pattern.length()>text.length())
			return a;
		int i=pattern.length()-1;
		int j = pattern.length()-1;
		while(i<text.length())
		{		
			if(text.charAt(i)== pattern.charAt(j))
			{
				if(j==0)
				{
					a.add(i);	
					j = pattern.length()-1;
					i= i + pattern.length();
				}
				else 
				{
					i--;
					j--;
				}
			}
			else
			{				
				i = i + pattern.length() -j + map[text.charAt(i)]-1;
				j=pattern.length()-1; 
			}
		}
		return a;
	}
	/**
	 * Creates a table of distances from each character to the end.
	 * Has an entry for every character from 0 to Character.MAX_VALUE.
	 * For use with Boyer-Moore.
	 *
	 * If the character is in the string:
	 * 		map[c] = length - last index of c - 1
	 * Otherwise:
	 * 		map[c] = length
	 *
	 * @param pattern
	 * The string being searched for
	 * @return
	 * An int array for Boyer-Moore
	 */
	public static int[] buildBoyerMooreCharTable(String pattern) {
		int[] map = new int[Character.MAX_VALUE + 1];
		for(int i = 0; i<map.length; i++){
			map[i] = pattern.length();
		}
		for(char c : pattern.toCharArray()){
			map[c] = Math.max(1, pattern.length()-pattern.lastIndexOf(c)-1);
		}
		return map;
	}



	/**
	 * Find all instances of pattern in text using Knuth-Morris-Pratt algorithm.
	 * Use buildKmpSuffixTable to build your reference table.
	 * 
	 * @param pattern
	 * The String to find
	 * @param test
	 * The String to look through
	 * @return
	 * A List of starting indices where pattern was found in text
	 */
	public static List<Integer> kmp(String pattern, String text) {
		int[] map = buildKmpSuffixTable(pattern);
		ArrayList<Integer> a = new ArrayList();
		if(pattern.length()>text.length())
			return a;
		int i =0;
		int j = 0;
		while(i<text.length())
		{
			if(pattern.charAt(j)== text.charAt(i))
			{
				if(j==pattern.length()-1 && pattern.length()==1)
				{
					a.add(i-pattern.length()+1);
					j=0;
					i++;
				}
				else if(j==pattern.length()-1)
				{
					a.add(i-pattern.length()+1);
					j= map[j];
				}
				else
				{
					i++;
					j++;
				}
				
			}
			else if(j!=0)
			{
				j= map[j];
			}
			else
			{
				i=i+1;
			}
		}
		return a;
	}

	/**
	 * Creates a table of matching suffix and prefix sizes.
	 * For use with Knuth-Morris-Pratt.
	 *
	 * If i = 0:
	 * 		map[i] = -1
	 * If i > 0:
	 * 		map[i] = size of largest common prefix and suffix for substring of size i
	 *
	 * @param pattern
	 * The string bing searched for
	 * @return
	 * An int array for Knuth-Morris-Pratt
	 */
	public static int[] buildKmpSuffixTable(String pattern) {
	int[] map = new int[pattern.length()];
		
		for(int i=0; i<pattern.length(); i++)
		{
			if(i==0)
				map[i]=-1;
			else if(i==1)
				map[i]=0;
			else
			{
				String s1= pattern.substring(0, i);
				int k = s1.length();
				int flag =0;
				while(s1.length()-k < s1.length()-1 && flag==0)
				{
					String temp1 =  s1.substring(0, k-1);
					String temp2 = s1.substring(s1.length()-k+1, s1.length());
					if(temp1.equals(temp2))
					{
						map[i] = k-1;
						flag =1;
					}
					else
					{
						k--;
					}
				}
				if(flag ==0)
					map[i] = 0;
			}	
		}
		return map;
	}

	// This is the base to be used for Rabin-Karp. No touchy.
	private static final int BASE = 997;

	/**
	 * Find all instances of pattern in text using Rabin-Karp algorithm.
	 * Use hashString and updateHash to handle hashing.
	 *
	 * @param pattern
	 * The String to find
	 * @param test
	 * The String to look through
	 * @return
	 * A List of starting indices where pattern was found in text
	 */
	public static List<Integer> rabinKarp(String pattern, String text) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		if(pattern.length()>text.length())
			return a;
		int firstHash = hashString(text.substring(0,pattern.length()));
		int patternHash= hashString(pattern);
		if(firstHash == patternHash)
			a.add(0);
		int tempHash=firstHash;
		for(int i=1; i<=text.length()- pattern.length(); i++)
		{
			tempHash = updateHash(tempHash, text.charAt(i + pattern.length() -1), text.charAt(i-1), pattern.length());
			if(tempHash == patternHash)
				a.add(i);
		}
		return a;
	}

	/**
	 * Hashes a string in a specified way.
	 * For use with Rabin-Karp.
	 *
	 * This hash function will use BASE and the indices of the characters.
	 * Each character at i is multiplied by BASE raised to the power of length - 1 - i
	 * These values are summed to determine the entire hash.
	 *
	 * For example:
	 * Hashing "bunn" as a substring of "bunny" with base 433
	 * hash = b * 433 ^ 3 + u * 433 ^ 2 + n * 433 ^ 1 + n * 433 ^ 0
	 *      = 98 * 433 ^ 3 + 117 * 433 ^ 2 + 110 * 433 ^ 1 + 110 * 433 ^ 0
	 *      = 7977892179
	 *
	 * @param pattern
	 * The String to be hashed
	 * @return
	 * A hash value for the string
	 */
	public static int hashString(String pattern) {
		int hash=0;
		for(int i=0; i<pattern.length(); i++)
		{
			hash = (int) (hash + power(BASE, pattern.length()-i-1)*pattern.charAt(i));
		}
		return hash;
	}

	/**
	 * Updates the oldHash in a specified way.
	 * Follows the same hash formula as hashString.
	 * For use with Rabin-Karp.
	 *
	 * To update the hash, remove the oldChar times BASE raised to the length - 1,
	 * 		multiply by BASE, and add the newChar.
	 * For example:
	 * Shifting from "bunn" to "unny" in "bunny" with base 433
	 * hash("unny") = (hash("bunn") - b * 433 ^ 3) * 433 + y * 433 ^ 0
	 *              = (7977892179 - 98 * 433 ^ 3) * 433 + 121 * 433 ^ 0
	 *              = 9519051770
	 *
	 * @param oldHash
	 * The old hash to be updated
	 * @param newChar
	 * The new character added at the end of the substring
	 * @param oldChar
	 * The old character being removed from the front of the substring
	 * @param length
	 * The length of the hashed substring
	 */
	public static int power(int x, int y)
	{
		int ans=1;
		if(y==0)
			return 1;
		for(int i=0; i<y; i++)
		{
			ans = ans*x;
		}
		return ans;
	}
	
	public static int updateHash(int oldHash, char newChar, char oldChar, int length) {
		return (int) (oldHash - (oldChar*power(BASE, length-1)))*BASE + newChar;
	}
}