package price.calculator.mkm.ga;

import java.util.ArrayList;
import java.util.TreeMap;

import price.calculator.mkm.models.Card;
import price.calculator.mkm.models.Offer;

public class GA {

	private String[] sellers;
	private double[][] priceConstraints;
	private int[][] availabilityConstraints;

	private TreeMap<String, TreeMap<String, Offer>> listOfOffers;
	private ArrayList<Card> cardList;

	/* POPULATION */
	private ArrayList<indiv> parents;
	private ArrayList<indiv> offspring;

	/* PARAMETERS */
	int popSize = 350;
	int nbOffspring = 300;

	double currentBestFitness = -1;
	int numberOfGenerationsWithoutChange = 1000;
	int currentNumberOfGenerationsWithoutChange = 0;

	public GA(TreeMap<String, TreeMap<String, Offer>> listOfOffers, ArrayList<Card> cardList, String[][] sellers) {
		this.listOfOffers = listOfOffers;
		this.cardList = cardList;

		this.sellers = new String[listOfOffers.keySet().size()];
		this.priceConstraints = new double[listOfOffers.keySet().size()][cardList.size()];
		this.availabilityConstraints = new int[listOfOffers.keySet().size()][cardList.size()];

		for ( int sellerid = 0; sellerid < sellers.length; sellerid++ ) {
			this.sellers[sellerid] = sellers[sellerid][0];
			for ( int i = 0; i < cardList.size(); i++ ) {
				priceConstraints[sellerid][i] = listOfOffers.get( this.sellers[sellerid] ).get( cardList.get( i ).getName() ).getPrice();
				availabilityConstraints[sellerid][i] = listOfOffers.get( this.sellers[sellerid] ).get( cardList.get( i ).getName() ).getStock();
			}
		}
	}

	public void run() {
		// init population
		initParentPopulation();

		// evolutionary loop
		while ( currentNumberOfGenerationsWithoutChange < numberOfGenerationsWithoutChange ) {
			// create offspring population
			this.offspring = new ArrayList<indiv>();
			createChildren();

			mixPops();

			// deterministic replacement
			this.parents = new ArrayList<indiv>();
			replacement();

			// stopping criterion: 100 generations without change
			double bestFitness = getBestIndiv().getFitness();
			if ( currentBestFitness != bestFitness ) {
				currentBestFitness = bestFitness;
				currentNumberOfGenerationsWithoutChange = 0;
			}
			else {
				currentNumberOfGenerationsWithoutChange++;
			}
		}
	}

	public void initParentPopulation() {
		this.parents = new ArrayList<indiv>();
		for ( int i = 0; i < popSize; i++ ) {
			indiv in = new indiv( this );
			in.initialize();
			in.evaluate();
			this.parents.add( in );
		}
	}

	public void createChildren() {

		for ( int i = 0; i < this.nbOffspring; i++ ) {
			int parent1 = tournament( parents, 2 );
			int parent2 = tournament( parents, 2 );

			indiv in = new indiv( this );
			in.Crossover( parents.get( parent1 ), parents.get( parent2 ) );
			in.mutate();
			in.evaluate();
			this.offspring.add( in );
		}
	}

	public void mixPops() {
		for ( int i = 0; i < popSize; i++ ) {
			this.offspring.add( this.parents.get( i ) );
		}
	}

	public void replacement() {
		for ( int i = 0; i < popSize; i++ ) {
			int bestIndiv = getBestIndiv( offspring );
			this.parents.add( offspring.get( bestIndiv ) );
			offspring.remove( bestIndiv );
		}
	}

	public int tournament(ArrayList<indiv> pop, int pressure) {
		// initial indiv
		int bestIndiv = Random.random( 0, pop.size() - 1 );
		for ( int i = 1; i < pressure; i++ ) {
			int selectedIndiv = Random.random( 0, pop.size() - 1 );
			if ( pop.get( selectedIndiv ).getFitness() < pop.get( bestIndiv ).getFitness() )
				bestIndiv = selectedIndiv;
		}

		return bestIndiv;
	}

	public int getBestIndiv(ArrayList<indiv> pop) {
		int bestIndiv = 0;
		for ( int j = 1; j < pop.size(); j++ ) {
			if ( pop.get( j ).getFitness() < pop.get( bestIndiv ).getFitness() )
				bestIndiv = j;
		}
		return bestIndiv;
	}

	public indiv getBestIndiv() {
		int bestIndiv = 0;
		for ( int j = 1; j < parents.size(); j++ ) {
			if ( parents.get( j ).getFitness() < parents.get( bestIndiv ).getFitness() )
				bestIndiv = j;
		}
		return parents.get( bestIndiv );
	}

	public String[] getSellers() {
		return this.sellers;
	}

	public double[][] getPriceConstraints() {
		return this.priceConstraints;
	}

	public int[][] getAvailabilityConstraints() {
		return this.availabilityConstraints;
	}

	public int getNumberOfCards() {
		return this.cardList.size();
	}

	public ArrayList<Card> getCardList() {
		return this.cardList;
	}

	public TreeMap<String, TreeMap<String, Offer>> getListOfOffer() {
		return this.listOfOffers;
	}

}
