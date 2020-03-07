import domain.Inventory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoSessionApi implements Runnable {

    private Socket clientSocket;

    public EchoSessionApi(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
        ) {

            String inputLine, outputLine;

            if ((inputLine = in.readLine()) != null) {
                String productById = ApiClient.sendGet("http://172.16.1.71:5080/products/id/" + inputLine);

                if (!productById.equalsIgnoreCase("null\n")) {
                    String inventory = ApiClient.sendGet("http://172.16.1.71:5080/inventory/id/" + inputLine);
                    Inventory inventoryItem = EchoProtocol.processInventory(inventory).get(0);
                    outputLine = "Item " + inputLine + " Found | " + inventoryItem;
                } else {
                    outputLine = "Item " + inputLine + " NOT Found";
                }

                out.println(outputLine);
            }
        } catch (IOException e) {
            LogUtil.log("Exception caught when trying to listen on port or listening for a connection");
            LogUtil.log(e.getMessage());
        }
    }
}
