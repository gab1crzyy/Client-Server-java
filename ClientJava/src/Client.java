import java.io.*;
import java.net.*;
import java.util.Scanner;
public class Client {

	public static void main(String[] args) {
		String serverAddress = "10.130.1.127"; // Indirizzo IP del server
        int port = 12345;                  // Porta del server
        
       
        try (Socket socket = new Socket(serverAddress, port)) {
            System.out.println("Connessione al server " + serverAddress + " sulla porta " + port);
            
            
            Scanner scanner = new Scanner(System.in);
            String nome = scanner.nextLine();
           

            // Stream di input/output per comunicare con il server
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input)); //converte in caratteri leggibili

            // Messaggio da inviare al server
            String message = nome;
            writer.println(message);
            System.out.println("Inviato al server: " + message);

            // Ricezione della risposta dal server
            String response = reader.readLine();
            System.out.println("Risposta dal server: " + response);
           

        } catch (UnknownHostException ex) {
            System.err.println("Host sconosciuto: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Errore di I/O: " + ex.getMessage());
        }
    }
		
	}


