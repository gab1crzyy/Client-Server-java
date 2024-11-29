package serverPackage;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    public static void main(String[] args) {
        int port = 12345; // Porta su cui il server ascolta
        List<Socket> clientSockets = new ArrayList<>();
        AtomicBoolean running = new AtomicBoolean(true); // Usa AtomicBoolean per la variabile condivisa

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server avviato e in ascolto sulla porta " + port);

            // Thread separato per leggere i comandi dalla console
            Thread consoleThread = new Thread(() -> {
                try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
                    while (running.get()) { // Usa running.get() per controllare il valore
                        String command = consoleReader.readLine();
                        if ("exit".equalsIgnoreCase(command)) {
                            System.out.println("Chiusura del server in corso...");
                            running.set(false); // Imposta running a false

                            // Invia messaggio di chiusura ai client
                            synchronized (clientSockets) {
                                for (Socket client : clientSockets) {
                                    try (PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                                        out.println("Server in chiusura. Connessione terminata.");
                                    } catch (IOException e) {
                                        System.err.println("Errore durante l'invio del messaggio di chiusura: " + e.getMessage());
                                    }
                                }
                            }

                            // Chiudi il ServerSocket per interrompere il metodo accept()
                            try {
                                serverSocket.close();
                            } catch (IOException e) {
                                System.err.println("Errore durante la chiusura del ServerSocket: " + e.getMessage());
                            }
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Errore nella lettura dei comandi dalla console: " + e.getMessage());
                }
            });
            consoleThread.start();

            // Ciclo principale del server per gestire le connessioni dei client
            while (running.get()) { // Usa running.get() per verificare se il server è in esecuzione
                try {
                    System.out.println("In attesa di una connessione...");
                    Socket clientSocket = serverSocket.accept();

                    // Aggiungi il client alla lista
                    synchronized (clientSockets) {
                        clientSockets.add(clientSocket);
                    }

                    System.out.println("Connessione accettata da " + clientSocket.getInetAddress());

                    // Gestione della comunicazione con il client in un thread separato
                    new Thread(() -> {
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                            String clientMessage;
                            while ((clientMessage = in.readLine()) != null) {
                                System.out.println("Messaggio ricevuto: " + clientMessage);

                                // Trasforma il messaggio in maiuscolo e invialo al client
                                String upperCaseMessage = clientMessage.toUpperCase();
                                out.println("Messaggio trasformato: " + upperCaseMessage);
                            }
                        } catch (IOException e) {
                            System.err.println("Errore nella comunicazione con il client: " + e.getMessage());
                        } finally {
                            // Rimuovi il client dalla lista quando si disconnette
                            synchronized (clientSockets) {
                                clientSockets.remove(clientSocket);
                            }
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                System.err.println("Errore nella chiusura della connessione con il client: " + e.getMessage());
                            }
                            System.out.println("Connessione chiusa con il client.");
                        }
                    }).start();
                } catch (IOException e) {
                    if (running.get()) { // Se il server non è in chiusura, stampa l'errore
                        System.err.println("Errore durante l'accettazione di una connessione: " + e.getMessage());
                    } else {
                        System.out.println("Server in chiusura, accettazione connessioni interrotta.");
                    }
                }
            }
            //banan.java

            // Chiudi il server dopo il comando "exit"
            consoleThread.join();
            System.out.println("Server chiuso.");
        } catch (IOException | InterruptedException e) {
            System.err.println("Errore nel server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
