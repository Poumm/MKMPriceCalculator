package price.calculator.mkm.parsing;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import price.calculator.mkm.models.Offer;

public class ParseHTMLOffer {

	public static Offer parseOffer(StringBuffer inputHtml, String cardName) {
		double bestPrice = Double.MAX_VALUE;
		int stockNumber = 0;

		Document doc = Jsoup.parse( String.valueOf( inputHtml ) );
		Elements india = doc.select( "div.row" );
		for ( Element table : doc.select( "table" ) ) {
			for ( Element row : table.select( "tr" ) ) {
				Elements tds = row.select( "td" );
				if ( tds.size() == 16 ) {
					if ( nameValid( tds.get( 2 ).text(), cardName ) && languageValid( tds.get( 5 ).html() ) && qualityValid( tds.get( 6 ).html() ) ) {
						String stock = tds.get( 14 ).text();
						String price = tds.get( 13 ).text();
						if ( price.contains( "PU:" ) ) {
							price = price.substring( price.indexOf( ":" ) + 2, price.length() - 2 );
						}
						else {
							price = price.substring( 0, tds.get( 13 ).text().length() - 2 );

						}
						price = price.replaceAll( ",", "." );
						Double priced = Double.parseDouble( price );

						if ( priced < bestPrice ) {
							bestPrice = priced;
							stockNumber = Integer.parseInt( stock );
						}
					}
				}
			}
		}

		if ( bestPrice == Double.MAX_VALUE ) {
			bestPrice = 0.0;
		}

		return new Offer( cardName, bestPrice, stockNumber );
	}

	public static boolean nameValid(String line, String cardName) {
		if ( line.contains( cardName ) ){
			return true;
		} else {
			return false;
		}
	}

	public static boolean languageValid(String line) {
		if ( line.contains( "Anglais" ) || line.contains( "Francais" ) ) {
			return true;
		}
		return false;
	}

	public static boolean qualityValid(String line) {
		if ( line.contains( "Mint" ) || line.contains( "Near Mint" ) ) {
			return true;
		}
		return false;
	}
}
