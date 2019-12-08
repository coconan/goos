package auctionsniper;

import static auctionsniper.UserRequestListener.Item;

public interface AuctionHouse {
    Auction auctionFor(Item item);
    void disconnect();
}
