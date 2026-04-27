// package ddc.server.controller.handler;

// import java.util.List;
// import com.google.gson.Gson;

// import ddc.server.config.GsonConfig;
// import ddc.server.controller.RequestMessage;
// import ddc.server.controller.service.ItemService;
// import ddc.server.dto.ItemDTO;
// import ddc.server.model.item.ItemGeneric;




// public class GetItemsHandler implements ActionHandler{
//       private final ItemService itemService = new ItemService();
//       private final Gson gson = GsonConfig.newGson();

//       @Override
//       public String handle(RequestMessage request){
//           List<ItemGeneric> items = new itemService.getAllItems();

//           List<ItemDTO> itemDTOs = items.stream().map(ItemDTO::fromItem).toList();
//           return gson.toJson(itemDTOs);

//       }


// }
