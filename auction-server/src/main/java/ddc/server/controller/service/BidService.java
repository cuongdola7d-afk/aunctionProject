package ddc.server.controller.service;

import java.util.List;

import ddc.server.dao.BidDAO;
import ddc.server.model.transaction.Bid;

public class BidService {
    private final BidDAO bidDAO;

    public BidService() {
        this.bidDAO = new BidDAO();
    }

    public List<Bid> getAll(String username) {
        return bidDAO.getAllUserBid(username);
    }
}
