package price.calculator.mkm.ga;

public class Random {

	public static int random(int Min, int Max) {
		return Min + (int) ( Math.random() * ( ( Max - Min ) + 1 ) );
	}

}
