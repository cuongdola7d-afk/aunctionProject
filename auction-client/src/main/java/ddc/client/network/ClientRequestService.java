// package ddc.client.network;
// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.io.PrintWriter;
// import java.lang.reflect.Type;
// import java.net.Socket;
// import java.util.Collection;
// import java.util.List;

// import javax.xml.crypto.KeySelector.Purpose;

// import com.google.gson.Gson;
// import com.google.gson.reflect.TypeToken;

// import ddc.client.model.ItemDTO;
// import ddc.server.controller.RequestMessage;

// public class ClientRequestService {
//       private static final String HOST = "localhost";
//       private static final int PORT = 12345;
      
//       private static final Gson gson = new Gson();

//       private ClientRequestService(){}

//       public static List<ItemDTO> getItems(){
//           try (Socket socket = new Socket(HOST, PORT);
//              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//              PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                  
//               RequestMassage request = new RequestMassage("GET_ITEMS", null);
//               out.println(gson.toJson(request));

//               String responseJson = in.readLine();
              
//               Type listType = new TypeToken<List<ItemDTO>>() {}.getType();
//               request gson.fromJson(responseJson, listType);
//              } catch (Exception e){
//                  System.out.println("Không lấy được danh sách item từ server");
//                  e.printStackTrace();
//                  return Collections.emptyList();
//              }
         
//       }
// }
