package price.calculator.mkm.models;

public class Card {

	private String name;
	private int number;

	public Card(String name, int number) {
		this.name = name;
		this.number = number;
	}

	public String getName() {
		return this.name;
	}

	public int getNumber() {
		return this.number;
	}

}
