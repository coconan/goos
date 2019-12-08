package auctionsniper.ui;

import auctionsniper.*;
import auctionsniper.util.Defect;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, SniperPortfolio.PortfolioListener {
    private static String[] STATUS_TEXT = {
            "Joining", "Bidding", "Winning", "Losing", "Lost", "Won"
    };
    private List<SniperSnapshot> snapshots = new ArrayList<>();

    @Override
    public int getRowCount() {
        return snapshots.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        int row = rowMatching(newSnapshot);
        snapshots.set(row, newSnapshot);
        fireTableRowsUpdated(row, row);
    }

    private void addSniperSnapshot(SniperSnapshot newSniper) {
        snapshots.add(newSniper);
        int row = snapshots.size() - 1;
        fireTableRowsInserted(row, row);
    }

    private int rowMatching(SniperSnapshot newSnapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (newSnapshot.isForSameItemAs(snapshots.get(i))) {
                return i;
            }
        }
        throw new Defect("Cannot find match for " + newSnapshot.itemId);
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    @Override
    public void sniperAdded(AuctionSniper sniper) {
        addSniperSnapshot(sniper.getSnapshot());
        sniper.addSniperListener(new SwingThreadSniperListener(this));
    }
}
