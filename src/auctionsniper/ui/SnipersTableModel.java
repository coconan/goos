package auctionsniper.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {
    private static String[] STATUS_TEXT = {
            MainWindow.STATUS_JOINING,
            MainWindow.STATUS_BIDDING,
            MainWindow.STATUS_WINNING
    };
    private static final SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
    private String statusText = STATUS_TEXT[SniperState.JOINING.ordinal()];
    private SniperSnapshot sniperSnapshot = STARTING_UP;

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (Column.at(columnIndex)) {
            case ITEM_IDENTIFIER:
                return sniperSnapshot.itemId;
            case LAST_PRICE:
                return sniperSnapshot.lastPrice;
            case LAST_BID:
                return sniperSnapshot.lastBid;
            case SNIPER_STATUS:
                return statusText;
            default:
                throw new IllegalArgumentException("No column at " + columnIndex);
        }
    }

    public void setStatusText(String newStatusText) {
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }

    public void sniperStatusChanged(SniperSnapshot newSnapshot) {
        sniperSnapshot = newSnapshot;
        statusText = STATUS_TEXT[newSnapshot.state.ordinal()];
        fireTableRowsUpdated(0, 0);
    }
}
