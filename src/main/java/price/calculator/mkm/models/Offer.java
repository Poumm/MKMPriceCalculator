package price.calculator.mkm.models;

public class Offer {

	private String card;

	private boolean isBestOffer = false;

	private double price;
	private int stock;

	private int numberOrder;

	public Offer(String card, double price, int stock) {
		this.card = card;
		this.price = price;
		this.stock = stock;
	}

	public int getStock() {
		return this.stock;
	}

	public double getPrice() {
		return this.price;
	}

	public void setBestOffer() {
		isBestOffer = true;
	}

	public boolean isBestOffer() {
		return isBestOffer;
	}

	public String toString() {
		String s = price + " x" + stock;
		return s;
	}
}
