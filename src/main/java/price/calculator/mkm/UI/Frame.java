package price.calculator.mkm.UI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import price.calculator.mkm.ga.GA;
import price.calculator.mkm.ga.GetPrice;
import price.calculator.mkm.ga.indiv;
import price.calculator.mkm.models.Card;
import price.calculator.mkm.models.Offer;

public class Frame extends JFrame {

	private JTable priceTable;
	private JTable orderTable;

	private String[][] sellers;
	private ArrayList<Card> cardList;

	private GetPrice getPrice;
	private JScrollPane scrollPanePriceTable;
	private JScrollPane scrollPaneOrderTable;
	private JSplitPane splitPaneCardlistTables;
	private JSplitPane tablesSplitPane;

	private TreeMap<String, TreeMap<String, Offer>> listOfOffers;
	private JScrollPane scrollPane_1;
	private JTextPane textPane;

	public Frame(String[][] sellers) throws KeyManagementException, NoSuchAlgorithmException {
		super( "Price Listing" );
		// this.setSize(1280, 1024);
		this.setDefaultCloseOperation( EXIT_ON_CLOSE );
		this.setExtendedState( JFrame.MAXIMIZED_BOTH );
		this.setLocationRelativeTo( null );

		this.sellers = sellers;
		this.cardList = new ArrayList<Card>();

		this.listOfOffers = new TreeMap<String, TreeMap<String, Offer>>();
		for ( int i = 0; i < sellers.length; i++ ) {
			listOfOffers.put( sellers[i][0], new TreeMap<String, Offer>() );
		}

		getPrice = new GetPrice();

		textPane = new JTextPane();
		scrollPane_1 = new JScrollPane( textPane );
		// getContentPane().add(scrollPane_1, BorderLayout.WEST);

		createEmptyTables();

		// createTable();
		JPanel panel = new JPanel();
		getContentPane().add( panel, BorderLayout.SOUTH );

		ActionListener radioButtonActionListener = new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try {
					findPrices();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		JButton btnNewButton = new JButton( "Find Prices" );
		panel.add( btnNewButton );

		tablesSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
		tablesSplitPane.setLeftComponent( scrollPanePriceTable );
		tablesSplitPane.setRightComponent( scrollPaneOrderTable );
		splitPaneCardlistTables = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		splitPaneCardlistTables.setLeftComponent( scrollPane_1 );
		splitPaneCardlistTables.setRightComponent( tablesSplitPane );
		getContentPane().add( splitPaneCardlistTables );
		splitPaneCardlistTables.setOneTouchExpandable( true );

		btnNewButton.addActionListener( radioButtonActionListener );

		this.setVisible( true );

		splitPaneCardlistTables.setResizeWeight( 0.5 );
	}

	private void createEmptyTables() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn( "Card amount" );
		model.addColumn( "Card name" );

		for ( int i = 0; i < this.sellers.length; i++ ) {
			model.addColumn( this.sellers[i][0] );
		}

		this.priceTable = new JTable( model ) {

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		this.priceTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		this.priceTable.setCellSelectionEnabled( true );

		this.scrollPanePriceTable = new JScrollPane( priceTable );
		// getContentPane().add(scrollPane);

		model = new DefaultTableModel();
		model.addColumn( "Card name" );
		for ( int i = 0; i < this.sellers.length; i++ ) {
			model.addColumn( this.sellers[i][0] );
		}
		model.addColumn( "Total" );
		this.orderTable = new JTable( model ) {

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		this.orderTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		this.orderTable.setCellSelectionEnabled( true );

		this.scrollPaneOrderTable = new JScrollPane( orderTable );

	}

	private void createPriceTable() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn( "Card amount" );
		model.addColumn( "Card name" );

		for ( int i = 0; i < this.sellers.length; i++ ) {
			model.addColumn( this.sellers[i][0] );
		}

		for ( int i = 0; i < this.cardList.size(); i++ ) {
			String[] data = new String[this.sellers.length + 2];
			data[0] = this.cardList.get( i ).getNumber() + "";
			data[1] = this.cardList.get( i ).getName();
			model.addRow( data );
		}

		this.priceTable = new JTable( model ) {

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		this.priceTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		this.priceTable.setCellSelectionEnabled( true );

		for ( int i = 0; i < this.priceTable.getColumnCount(); i++ )
			priceTable.getColumnModel().getColumn( i ).setCellRenderer( new PriceTableCellRenderer() );

		this.scrollPanePriceTable.setViewportView( priceTable );
	}

	private void createOrderTable(indiv in) {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn( "Card name" );
		for ( int i = 0; i < this.sellers.length; i++ ) {
			model.addColumn( this.sellers[i][0] );
		}
		model.addColumn( "Total" );

		for ( int i = 0; i < this.cardList.size(); i++ ) {
			String[] data = new String[this.sellers.length + 2];
			data[0] = this.cardList.get( i ).getName();
			int total = 0;
			for ( int j = 0; j < sellers.length; j++ ) {
				if ( in.getGenome()[j][i] > 0 )
					data[j + 1] = in.getGenome()[j][i] + " x " + listOfOffers.get( sellers[j][0] ).get( data[0] ).getPrice();
				else
					data[j + 1] = "";
				total += in.getGenome()[j][i];
			}
			data[this.sellers.length + 1] = total + "";
			model.addRow( data );
		}

		// add final price
		String[] data = new String[this.sellers.length + 2];
		data[0] = "Total";
		double total = 0;
		for ( int j = 0; j < sellers.length; j++ ) {
			double price = in.getPrice( j );
			if ( price > 0.0 )
				data[j + 1] = price + "";
			else
				data[j + 1] = "";
			total += price;
		}
		data[this.sellers.length + 1] = total + "";
		model.addRow( data );

		this.orderTable = new JTable( model ) {

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		this.orderTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		this.orderTable.setCellSelectionEnabled( true );

		// for(int i=0; i<this.priceTable.getColumnCount();
		// i++)priceTable.getColumnModel().getColumn(i).setCellRenderer(new PriceTableCellRenderer());

		this.scrollPaneOrderTable.setViewportView( orderTable );
	}

	private void determineBestPrice(String card) {
		Double bestPrice = Double.MAX_VALUE;
		String bestSeller = "";

		for ( int i = 0; i < sellers.length; i++ ) {
			Offer offer = listOfOffers.get( sellers[i][0] ).get( card );
			if ( offer.getPrice() != 0.0 && offer.getPrice() < bestPrice ) {
				bestSeller = sellers[i][0];
				bestPrice = offer.getPrice();
			}
		}

		if ( !bestSeller.equals( "" ) )
			listOfOffers.get( bestSeller ).get( card ).setBestOffer();

		( (DefaultTableModel) priceTable.getModel() ).fireTableDataChanged();
	}

	private void parseCards() {
		this.cardList = new ArrayList<Card>();
		String[] lines = textPane.getText().split( System.getProperty( "line.separator" ) );
		for ( int i = 0; i < lines.length; i++ ) {
			String line = lines[i];
			if ( !line.equals( "" ) ) {
				line = line.trim();
				line = line.replaceAll( "^\\s*", "" );
				String number = line.substring( 0, line.indexOf( "x" ) );
				String name = line.substring( line.indexOf( "x" ) + 2, line.length() );
				cardList.add( new Card( name, Integer.parseInt( number ) ) );
			}
		}
	}

	private void findPrices() throws IOException {
		
		Thread t = new Thread( new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				parseCards();
				createPriceTable();
				validate();

				for ( int i = 0; i < cardList.size(); i++ ) {
					for ( int j = 0; j < sellers.length; j++ ) {
						try {
							Offer offer = getPrice.getPrices( cardList.get( i ).getName(), sellers[j][1] );

							listOfOffers.get( sellers[j][0] ).put( cardList.get( i ).getName(), offer );
							priceTable.setValueAt( offer, i, 2 + j );
							Thread.sleep( 500 );

						}
						catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					determineBestPrice( cardList.get( i ).getName() );
				}
				GA ga = new GA( listOfOffers, cardList, sellers );
				ga.run();
				createOrderTable( ga.getBestIndiv() );
			}
		} );
		t.start();
	}
}
