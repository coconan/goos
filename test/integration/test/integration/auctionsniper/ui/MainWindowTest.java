package test.integration.auctionsniper.ui;

import auctionsniper.SniperPortfolio;
import auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;
import test.endtoend.auctionsniper.AuctionSniperDriver;

import static auctionsniper.UserRequestListener.Item;
import static org.hamcrest.core.IsEqual.equalTo;

public class MainWindowTest {
    private final MainWindow mainWindow = new MainWindow(new SniperPortfolio());
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<Item> itemProbe =
                new ValueMatcherProbe<>(equalTo(new Item("an item-id", 789)), "join request");

        mainWindow.addUserRequestListener(itemProbe::setReceivedValue);
        driver.startBiddingWithStopPrice("an item-id", 789);
        driver.check(itemProbe);
    }
}
