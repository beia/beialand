package hello;



import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;



public class TokenString {

	

	@JacksonXmlText(value = true)

	private String tokenString;



	public String getTokenString() {

		return tokenString;

	}



	public void setTokenString(String tokenString) {

		this.tokenString = tokenString;

	}



}