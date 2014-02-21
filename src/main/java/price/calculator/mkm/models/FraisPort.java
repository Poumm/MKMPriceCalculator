package price.calculator.mkm.models;

public class FraisPort {

	public static double ComputeFraisDePort(int numberOfCards) {
		if ( numberOfCards <= 4 )
			return 0.93;
		else if ( numberOfCards <= 17 )
			return 1.35;
		else if ( numberOfCards <= 40 )
			return 2.05;
		else if ( numberOfCards <= 107 )
			return 3.05;
		else
			return 3.9;
	}

}
