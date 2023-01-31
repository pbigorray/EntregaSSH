import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        int numPuerto = 8080;
        try (ServerSocket socketServidor = new ServerSocket(numPuerto)) {
            System.out.printf("Creado socket de servidor en puerto %d. Esperando conexiones de clientes.\n", numPuerto);
            // Acepta una conexión de cliente tras otra
            while (true) {
                try (Socket socketComunicacion = socketServidor.accept()) {
                    System.out.printf("Cliente conectado desde %s:%d.\n", socketComunicacion.getInetAddress().getHostAddress(), socketComunicacion.getPort());
                    try (InputStream is = socketComunicacion.getInputStream();
                         ObjectInputStream oisCliente = new ObjectInputStream(is);) {

                        System.out.println("mensaje tururuuu");
                        Mensaje objectoCli;

                        while ((objectoCli = (Mensaje) oisCliente.readObject()) != null) {
                            System.out.println(objectoCli);
                            String str = "";

                            try (BufferedReader br = new BufferedReader(new FileReader(objectoCli.getFile()))) {
                                while (br.ready())
                                    str += br.readLine();
                                File f = new File("/home/usuario/.ssh/know_hosts");

                                try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
                                    bw.append("\n");
                                    bw.append(str);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Cliente desconectado.");
            }
        } catch (IOException ex) {
            System.out.println("Excepción de E/S");
            ex.printStackTrace();
            System.exit(1);
        }

    }
}
