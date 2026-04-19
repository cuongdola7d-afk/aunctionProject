package ddc.server.controller.handler;

import com.google.gson.Gson;

import ddc.server.config.GsonConfig;
import ddc.server.controller.RequestMessage;
import ddc.server.dao.ItemDAO;
import ddc.server.model.item.Item;

public class AddItemHandler implements ActionHandler{
    private final Gson gson = GsonConfig.newGson();
    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    public String handle (RequestMessage request) {
        Item requestItem = gson.fromJson(request.getData(), Item.class);

        System.out.println("Adding: " + requestItem.getItemName());

        boolean isSuccess = itemDAO.addItem(requestItem);

        if (isSuccess) {
            System.out.println("Success!");
            return "\"SUCCESS\"";
        } else {
            System.out.println("Fail!");
            return "\"FAIL\"";
        }
    }
}
