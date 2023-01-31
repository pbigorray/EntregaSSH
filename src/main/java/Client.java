import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;

public class Client {
    public static void main(String[] args) throws IOException {
        String dir = "/home/usuario/.ssh/";
        String file = "id_rsa";
        File f = new File(dir + file);
        if (f.exists()) {
            Path path = Paths.get(f.getAbsolutePath());
            BasicFileAttributes basic = Files.readAttributes(path, BasicFileAttributes.class);
            FileTime time = basic.creationTime();
            int days = (int) (new Date().getTime() - new Date(time.toMillis()).getTime()) / 86400000;
            System.out.println("Dias de antiguadad: " + days);

            if (days < 30) {
                System.out.println("Tiene menos de 30 dias de antiguedad");
            } else {
                System.out.println("Tiene mas de 30 dias de antiguedad");
                f.delete();
                createKeys(dir + file);
            }
            try {
                createConnectionSSH(dir + file);
            } catch (Exception e) {
                System.out.println(e);
                f.delete();
                createKeys(dir + file);
                createConnectionServer(dir + file);
                createConnectionSSH(dir + file);
            }
        } else {
            createKeys(dir + file);
            System.out.println(dir + file + ".pub");
            createConnectionServer(dir + file);
            createConnectionSSH(dir + file);
        }
    }


    private static void createKeys(String file) {
        JSch jsch = new JSch();
        String comment = "pbigorray@debian11";
        try {
            KeyPair kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA, 2048);

            kpair.writePrivateKey(System.out);
            kpair.writePublicKey(System.out, comment);

            kpair.writePrivateKey(file);
            kpair.writePublicKey(file + ".pub", comment);
            System.out.println("Finger print: " + kpair.getFingerPrint());
            kpair.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createConnectionSSH(String privateKey) {
        try {
            JSch jsch = new JSch();
            String user = "usuario";
            String host = "127.0.0.1";
            int port = 22;
            jsch.addIdentity(privateKey);
            System.out.println("identity added ");
            Session session = jsch.getSession(user, host, port);

// Si es necesario introducir el password para iniciar sesion //
            session.setPassword("usuario");

// Para permitir conectarse sin comprobar el host

            session.setConfig("StrictHostKeyChecking", "no");
            System.out.println("session created."); // Conectamos

            session.connect();
            System.out.println("session connected.....");
            Channel channel = session.openChannel("shell");
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);
            channel.connect(3 * 1000);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private static void createConnectionServer(String path) {
        File file = new File(path + ".pub");
        int puerto = 8080;
        String host = "127.0.0.1";
        try (Socket echoSocket = new Socket(host, puerto);) {
            System.out.println("Conexión hecha");
            Mensaje m = new Mensaje(file);
            OutputStream os = echoSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(m);

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + host);
        }
    }
}
