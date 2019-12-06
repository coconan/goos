package test.auctionsniper;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SniperSnapshotTest {

    @Test
    public void transitionBetweenStates() {
        final String itemId = "item id";

        SniperSnapshot joining = SniperSnapshot.joining(itemId);
        assertEquals(new SniperSnapshot(itemId, 0, 0, SniperState.JOINING), joining);

        SniperSnapshot bidding = joining.bidding(123, 234);
        assertEquals(new SniperSnapshot(itemId, 123, 234, SniperState.BIDDING), bidding);
        assertEquals(new SniperSnapshot(itemId, 456, 234, SniperState.WINNING), bidding.winning(456));
    }
}
