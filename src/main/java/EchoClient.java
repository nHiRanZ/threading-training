/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.stream.IntStream;

public class EchoClient {
    public static void main(String[] args) throws IOException {

        String hostName = "localhost";
        int portNumber = 9095;

        int iterations = 10;

        IntStream.range(0, iterations)
                .forEach(value -> {
                    Thread thread = new Thread(() -> {
                        makeClientConn(portNumber, hostName, value);
                    });
                    thread.setName("client-" + value);
                    thread.start();
                });
    }

    private static void makeClientConn(int portNumber, String hostName, int iteration) {
//        LogUtil.log("Client Index: " + this.clientIndex);
        try (
                Socket echoClientSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(echoClientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(echoClientSocket.getInputStream()));
        ) {
            BufferedReader stdIn =
                    new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;

            while (true) {
//                fromUser = stdIn.readLine();
                fromUser = String.valueOf(iteration);
                LogUtil.log("Client Requested Product ID: " + fromUser);

                out.println(fromUser);
                fromServer = in.readLine();

                if (fromServer != null) {
                    LogUtil.log("Server: " + fromServer);
                }

                if (fromServer == null || fromServer.contains("BYE."))
                    break;

            }
        } catch (UnknownHostException e) {
            LogUtil.log("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            LogUtil.log("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}