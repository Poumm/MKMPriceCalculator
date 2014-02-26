package price.calculator.mkm.parsing;

import java.util.ArrayList;

import price.calculator.mkm.models.Card;


public class ParseCards {

	public static ArrayList<Card> parseCards(String input){
		ArrayList<Card> cardList = new ArrayList<Card>();
		String[] lines = input.split( System.getProperty( "line.separator" ) );
		for ( int i = 0; i < lines.length; i++ ) {
			String line = lines[i];
			if ( !line.equals( "" ) ) {
				line = line.trim();
				line = line.replaceAll( "^\\s*", "" );
				String number = line.substring( 0, line.indexOf( "x" ) );
				String name = line.substring( line.indexOf( "x" ) + 2, line.length() );
				cardList.add( new Card( name, Integer.parseInt( number ) ) );
			}
		}
		return cardList;
	}
}
