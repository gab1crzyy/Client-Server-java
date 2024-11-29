package serverPackage;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        int port = 12345;  // Porta su cui il server ascolta
        boolean running = true;
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in)); // Dichiarato all'esterno

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);  // Forza il riutilizzo della porta
            System.out.println("Server avviato e in ascolto sulla porta " + port);

            while (running) {
                System.out.println("In attesa di una connessione...");
                try (Socket clientSocket = serverSocket.accept()) {
                    // Informazioni del client
                    String clientAddress = clientSocket.getInetAddress().getHostAddress();
                    int clientPort = clientSocket.getPort();

                    // Chiedi se accettare la connessione
                    System.out.println("Connessione richiesta da [IP: " + clientAddress + ", Port: " + clientPort + "]");
                    System.out.print("Accettare la connessione (s/n)? ");
                    
                    // Leggi la risposta dalla console
                    String response = consoleReader.readLine();

                    if ("s".equalsIgnoreCase(response)) {
                        System.out.println("Connessione accettata da [IP: " + clientAddress + ", Port: " + clientPort + "]");
                        
                        // Comunica con il client
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                            boolean clientConnected = true;

                            while (clientConnected) {
                                // Leggi messaggi dal client
                                if (in.ready()) {
                                    String clientMessage = in.readLine();
                                    if (clientMessage == null) {
                                        System.out.println("Il client si è disconnesso.");
                                        clientConnected = false;
                                        continue;
                                    }

                                    System.out.println("Messaggio ricevuto da [IP: " + clientAddress + ", Port: " + clientPort + "]: " + clientMessage);

                                    if ("stop".equalsIgnoreCase(clientMessage)) {
                                        System.out.println("Il client ha inviato il comando 'stop'. Interruzione della connessione.");
                                        clientConnected = false;
                                        break;
                                    } else if ("exit".equalsIgnoreCase(clientMessage)) {
                                        System.out.println("Il client ha inviato il comando 'exit'. La connessione verrà chiusa, ma il server rimarrà attivo.");
                                        break;
                                    } else {
                                        // Trasforma il messaggio in maiuscolo e invialo al client
                                        String upperCaseMessage = clientMessage.toUpperCase();
                                        out.println("Messaggio trasformato: " + upperCaseMessage);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            System.err.println("Errore nella comunicazione con il client: " + e.getMessage());
                        }

                        // Messaggio finale con i dati del client
                        System.out.println("Connessione terminata con il client [IP: " + clientAddress + ", Port: " + clientPort + "]");
                    } else {
                        System.out.println("Connessione rifiutata da [IP: " + clientAddress + ", Port: " + clientPort + "]");
                    }
                } catch (IOException e) {
                    System.err.println("Errore durante l'accettazione di una connessione: " + e.getMessage());
                }

                // Verifica se l'utente ha scritto 'exit' nella console
                try {
                    if (consoleReader.ready()) {
                        String command = consoleReader.readLine();
                        if (command.equalsIgnoreCase("exit")) {
                            System.out.println("Chiusura del server in corso...");
                            running = false;
                            // Attendi un po' prima di chiudere la connessione
                            try {
                                Thread.sleep(2000);  // Ritardo di 2 secondi per dare tempo al sistema di rilasciare la porta
                            } catch (InterruptedException e) {
                                System.err.println("Errore durante l'attesa: " + e.getMessage());
                            }
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Errore durante la lettura della console: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nel server: " + e.getMessage());
        }

        System.out.println("Server chiuso.");
    }
}
