import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class EchoSession implements Runnable {

    private Socket clientSocket;

    public EchoSession(Socket clientSocket) {
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

            // Initiate conversation with client
//            KnockKnockProtocol kkp = new KnockKnockProtocol();
            EchoProtocol echoProtocol = new EchoProtocol();
//            outputLine = kkp.processInput(null);
//            out.println(outputLine);

            if ((inputLine = in.readLine()) != null) {
                LogUtil.log("Client Received");
                outputLine = echoProtocol.processInput(inputLine);
                TimeUnit.SECONDS.sleep(2);
                out.println(outputLine);
            }
        } catch (IOException e) {
            LogUtil.log("Exception caught when trying to listen on port or listening for a connection");
            LogUtil.log(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
