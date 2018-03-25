package hello;



import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;





public class Result {

	

	@JacksonXmlProperty(localName="string")

    private TokenString tokenString;



	public TokenString getTokenString() {

		return tokenString;

	}



	public void setTokenString(TokenString tokenString) {

		this.tokenString = tokenString;

	}



}