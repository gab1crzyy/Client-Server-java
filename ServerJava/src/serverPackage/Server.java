package serverPackage;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        int port = 12345; // Porta su cui il server ascolta
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server avviato e in ascolto sulla porta " + port);
            
            // Ciclo per accettare connessioni multiple
            while (true) {
                System.out.println("In attesa di una connessione...");
                
                // Accetta una connessione
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connessione accettata da " + clientSocket.getInetAddress());

                // Ottieni gli stream di input e output
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Leggi i dati inviati dal client
                String clientMessage = in.readLine();
                System.out.println("Messaggio ricevuto: " + clientMessage);

                // Trasforma il messaggio in maiuscolo
                String upperCaseMessage = clientMessage.toUpperCase();

                // Rispondi al client con il messaggio trasformato
                out.println("Messaggio trasformato: " + upperCaseMessage);

                // Chiudi la connessione con il client
                clientSocket.close();
                System.out.println("Connessione chiusa con il client.");
            }
        } catch (IOException e) {
            System.err.println("Errore nel server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
