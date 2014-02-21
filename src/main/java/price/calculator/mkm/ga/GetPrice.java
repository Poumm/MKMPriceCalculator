package price.calculator.mkm.ga;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import price.calculator.mkm.models.Offer;

public class GetPrice {

	private Proxy proxy;

	public GetPrice() throws NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };
		// Install the all-trusting trust manager
		SSLContext sc = null;
		sc = SSLContext.getInstance( "SSL" );
		sc.init( null, trustAllCerts, new java.security.SecureRandom() );

		HttpsURLConnection.setDefaultSSLSocketFactory( sc.getSocketFactory() );
		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {

			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier( allHostsValid );
		proxy = new Proxy( Proxy.Type.HTTP, new InetSocketAddress( "avcomws1.groupe-es.net", 8080 ) );
	}

	public Offer getPrices(String cardName, String merchantID) throws IOException {
		URL page = new URL( urlCreator( cardName, merchantID ) );
		// HttpsURLConnection yc = (HttpsURLConnection)page.openConnection(proxy);
		HttpsURLConnection yc = (HttpsURLConnection) page.openConnection();
		final Reader reader = new InputStreamReader( yc.getInputStream() );
		final BufferedReader br = new BufferedReader( reader );
		StringBuffer tmp = new StringBuffer();
		String line = "";
		while ( ( line = br.readLine() ) != null ) {
			tmp.append( line );
		}
		br.close();

		double bestPrice = Double.MAX_VALUE;
		int stockNumber = 0;

		Document doc = Jsoup.parse( String.valueOf( tmp ) );
		Elements india = doc.select( "div.row" );
		for ( Element table : doc.select( "table" ) ) {
			for ( Element row : table.select( "tr" ) ) {
				Elements tds = row.select( "td" );
				if ( tds.size() == 12 ) {
					if ( nameValid( tds.get( 2 ).text(), cardName ) && languageValid( tds.get( 5 ).html() ) && qualityValid( tds.get( 6 ).html() ) ) {
						String stock = tds.get( 10 ).text();
						String price = tds.get( 9 ).text();
						if ( price.contains( "PU:" ) ) {
							price = price.substring( price.indexOf( ":" ) + 2, price.length() - 2 );
						}
						else {
							price = price.substring( 0, tds.get( 9 ).text().length() - 2 );

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

		if ( bestPrice == Double.MAX_VALUE )
			bestPrice = 0.0;
		Offer offer = new Offer( cardName, bestPrice, stockNumber );

		return offer;
	}

	private boolean nameValid(String line, String cardName) {
		if ( line.equals( cardName ) )
			return true;
		return false;
	}

	private boolean languageValid(String line) {
		if ( line.contains( "Anglais" ) || line.contains( "Francais" ) ) {
			return true;
		}
		return false;
	}

	private boolean qualityValid(String line) {
		if ( line.contains( "Mint" ) || line.contains( "Near Mint" ) ) {
			return true;
		}
		return false;
	}

	private String urlCreator(String cardName, String merchantID) {
		String url = "https://fr.magiccardmarket.eu/?mainPage=browseUserProducts&idCategory=1&idUser=";
		url += merchantID;
		url += "&cardName=";
		url += cardName.replaceAll( " ", "+" );
		url += "&idExpansion=&idRarity=&idLanguage=&condition_uneq=&condition=&isFoil=0&isSigned=0&isPlayset=0&isAltered=0&comments=&minPrice=&maxPrice=";

		return url;
	}

}
