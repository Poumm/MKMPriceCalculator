package price.calculator.mkm.ga;

import java.util.ArrayList;

import price.calculator.mkm.models.FraisPort;

public class indiv {

	// GA
	private GA ga;

	private double fitness = 0.0; // bestPrice

	// Genome: stock choice
	private int[][] genome;
	private int[][] availableStock;

	public indiv(GA ga) {
		this.ga = ga;

		this.genome = new int[ga.getSellers().length][ga.getNumberOfCards()];
		this.availableStock = new int[ga.getSellers().length][ga.getNumberOfCards()];
	}

	public void initialize() {
		// For each card
		for ( int i = 0; i < ga.getNumberOfCards(); i++ ) {

			int availableStock = 0;
			int requiredStock = ga.getCardList().get( i ).getNumber();

			for ( int j = 0; j < ga.getSellers().length; j++ ) {
				this.availableStock[j][i] = ga.getAvailabilityConstraints()[j][i];
				availableStock += this.availableStock[j][i];
			}

			if ( availableStock < requiredStock ) {
				requiredStock = availableStock;
			}

			while ( requiredStock > 0 ) {
				int seller = Random.random( 0, ga.getSellers().length - 1 );
				if ( this.availableStock[seller][i] > 0 ) {
					this.availableStock[seller][i]--;
					this.genome[seller][i]++;
					requiredStock--;
				}
			}
		}
	}

	public void evaluate() {
		double pricePerSeller[] = new double[this.genome.length];
		// for each seller
		for ( int seller = 0; seller < this.genome.length; seller++ ) {
			double price = 0.0;
			int totalNumberCards = 0;
			// for each card
			for ( int card = 0; card < this.genome[seller].length; card++ ) {
				double sellerPrice = ga.getListOfOffer().get( ga.getSellers()[seller] ).get( ga.getCardList().get( card ).getName() ).getPrice();
				int numberOrderer = this.genome[seller][card];

				price += ( sellerPrice * numberOrderer );
				totalNumberCards += numberOrderer;
			}
			// compute frais de port
			if ( totalNumberCards > 0 )
				price += FraisPort.ComputeFraisDePort( totalNumberCards );
			pricePerSeller[seller] = price;
			this.fitness += price;
		}
	}

	public double getPrice(int seller) {
		double price = 0.0;
		int totalNumberCards = 0;
		// for each card
		for ( int card = 0; card < this.genome[seller].length; card++ ) {
			double sellerPrice = ga.getListOfOffer().get( ga.getSellers()[seller] ).get( ga.getCardList().get( card ).getName() ).getPrice();
			int numberOrderer = this.genome[seller][card];

			price += ( sellerPrice * numberOrderer );
			totalNumberCards += numberOrderer;
		}
		// compute frais de port
		if ( totalNumberCards > 0 )
			price += FraisPort.ComputeFraisDePort( totalNumberCards );
		return price;
	}

	public void Crossover(indiv p1, indiv p2) {
		// work on card basis
		for ( int i = 0; i < ga.getCardList().size(); i++ ) {
			// choose between p1 and p2
			if ( Math.random() < 0.5 ) {
				for ( int j = 0; j < this.genome.length; j++ ) {
					this.genome[j][i] = p1.getGenome()[j][i];
					this.availableStock[j][i] = p1.getAvailableStock()[j][i];
				}
			}
			else {
				for ( int j = 0; j < this.genome.length; j++ ) {
					this.genome[j][i] = p2.getGenome()[j][i];
					this.availableStock[j][i] = p2.getAvailableStock()[j][i];
				}
			}
		}
	}

	public void mutate() {
		// work on card basis
		for ( int i = 0; i < ga.getCardList().size(); i++ ) {
			if ( Math.random() < 0.1 ) {
				// find a seller with available stock
				ArrayList<Integer> poolOfSeller = new ArrayList<Integer>();
				for ( int j = 0; j < this.genome.length; j++ ) {
					if ( this.availableStock[j][i] > 0 )
						poolOfSeller.add( j );
				}

				ArrayList<Integer> poolOfOrders = new ArrayList<Integer>();
				for ( int j = 0; j < this.genome.length; j++ ) {
					if ( this.genome[j][i] > 0 )
						poolOfOrders.add( j );
				}

				if ( !poolOfSeller.isEmpty() && !poolOfOrders.isEmpty() ) {
					// choose a seller
					int seller = Random.random( 0, poolOfSeller.size() - 1 );
					int order = Random.random( 0, poolOfOrders.size() - 1 );

					this.genome[poolOfOrders.get( order )][i]++;
					this.availableStock[poolOfSeller.get( seller )][i]--;
				}
			}
		}
	}

	public double getFitness() {
		return this.fitness;
	}

	public int[][] getGenome() {
		return this.genome;
	}

	public int[][] getAvailableStock() {
		return this.availableStock;
	}
}
