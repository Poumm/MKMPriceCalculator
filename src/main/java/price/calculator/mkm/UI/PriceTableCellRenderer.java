package price.calculator.mkm.UI;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import price.calculator.mkm.models.Offer;

public class PriceTableCellRenderer extends DefaultTableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		Component cell = super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );

		DefaultTableModel model = (DefaultTableModel) table.getModel();

		if ( column > 1 ) {
			Offer offer = (Offer) model.getValueAt( row, column );
			if ( offer != null && offer.isBestOffer() ) {
				if ( isSelected ) {
					cell.setBackground( Color.decode( "#7FE817" ) );
				}
				else {
					cell.setBackground( Color.decode( "#CCFB5D" ) );
				}
			}
			else {
				if ( isSelected ) {
					cell.setBackground( Color.decode( "#E5E6E4" ) );
					cell.setForeground( Color.black );
				}
				else
					cell.setBackground( null );
			}
		}

		return cell;
	}

}
