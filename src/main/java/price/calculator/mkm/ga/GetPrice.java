package price.calculator.mkm.ga;

import static price.calculator.mkm.parsing.ParseHTMLOffer.parseOffer;
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
		//proxy = new Proxy( Proxy.Type.HTTP, new InetSocketAddress( "avcomws1.groupe-es.net", 8080 ) );
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

	

		return parseOffer(tmp, cardName);
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
