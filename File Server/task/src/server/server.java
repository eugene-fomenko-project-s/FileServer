package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class server {
    HashMap<Integer, String> IDENTY;
    final int PORT = 12123;
    final String ADDRESS = "127.0.0.1";
    final String DATA_DIR = "C:\\Users\\fomen\\IdeaProjects\\Car Sharing\\Car Sharing\\File Server\\File Server\\task\\src\\server\\data";
    int ID;
    String BY_WHAT;

    @SuppressWarnings("unchecked")
    server() {

        try (ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS))) {
            System.out.println("Server started!");
            while (true) {
                try (
                        Socket client = server.accept();
                        DataInputStream input = new DataInputStream(client.getInputStream());
                        DataOutputStream output = new DataOutputStream(client.getOutputStream())
                ) {
                    IDENTY = new HashMap<>();
                    int size = Runtime.getRuntime().availableProcessors();
                    ExecutorService service = Executors.newFixedThreadPool(size);
                    String msg = input.readUTF();
                    if (msg.equals("EXIT")) {
                        return;
                    }
                    switch (msg) {
                        case "GET":
                            File im = new File("C:\\Users\\fomen\\IdeaProjects\\Car Sharing\\Car Sharing\\File Server\\File Server\\task\\src\\server\\serial" + "\\" + "file.txt");
                            if (im.length() > 0) {
                                FileInputStream fileInputStream = new FileInputStream(im);
                                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                                IDENTY = (HashMap<Integer, String>) objectInputStream.readObject();
                            }
                            try {
                                BY_WHAT = input.readUTF();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            String name_file;
                            if (BY_WHAT.equals("BY_ID")) {
                                int id;
                                try {
                                    id = Integer.parseInt(input.readUTF());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                name_file = IDENTY.get(id);
                            } else {
                                try {
                                    name_file = input.readUTF();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            File a = new File(DATA_DIR + "\\" + name_file);
                            if (a.exists()) {
                                try {
                                    output.writeUTF("200");
                                    File file = new File(DATA_DIR + "\\" + name_file);
                                    byte[] array;
                                    try {
                                        array = Files.readAllBytes(Paths.get(String.valueOf(file)));
                                        output.writeInt(array.length);
                                        output.write(array);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                try {
                                    output.writeUTF("404");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            break;
                        case "PUT":
                            File im1 = new File("C:\\Users\\fomen\\IdeaProjects\\Car Sharing\\Car Sharing\\File Server\\File Server\\task\\src\\server\\serial" + "\\" + "file.txt");
                            if (im1.length() > 0) {
                                FileInputStream fileInputStream = new FileInputStream(im1);
                                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                                IDENTY = (HashMap<Integer, String>) objectInputStream.readObject();
                            }
                            String name;
                            try {
                                name = input.readUTF();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            if (name.equals("none")) {
                                name = "file_" + ID;
                            }
                            synchronized (this) {
                                for (HashMap.Entry<Integer, String> entry : IDENTY.entrySet()) {
                                    ID = entry.getKey();
                                }
                                ID = ID + 1;
                            }
                            File c = new File(DATA_DIR + "\\" + name);
                            byte[] message;
                            try {
                                int length = input.readInt();
                                message = new byte[length];
                                input.readFully(message, 0, message.length);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            if (c.exists()) {
                                try {
                                    output.writeUTF("403");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                try {
                                    File file = new File(DATA_DIR + "\\" + name);
                                    if (file.createNewFile()) {
                                        String finalName = name;
                                        service.submit(() -> {
                                            OutputStream outStream;
                                            try {
                                                outStream = new FileOutputStream(file);
                                            } catch (FileNotFoundException e) {
                                                throw new RuntimeException(e);
                                            }
                                            try {
                                                outStream.write(message);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            IDENTY.put(ID, finalName);
                                            FileOutputStream outputStream;
                                            try {
                                                outputStream = new FileOutputStream("C:\\Users\\fomen\\IdeaProjects\\Car Sharing\\Car Sharing\\File Server\\File Server\\task\\src\\server\\serial" + "\\" + "file.txt");
                                            } catch (FileNotFoundException e) {
                                                throw new RuntimeException(e);
                                            }
                                            ObjectOutputStream objectOutputStream;
                                            try {
                                                objectOutputStream = new ObjectOutputStream(outputStream);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            try {
                                                objectOutputStream.writeObject(IDENTY);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            try {
                                                objectOutputStream.close();
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            try {
                                                outStream.close();
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        });
                                        output.writeUTF("200");
                                        output.writeInt(ID);
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            break;
                        case "DELETE":
                            File im2 = new File("C:\\Users\\fomen\\IdeaProjects\\Car Sharing\\Car Sharing\\File Server\\File Server\\task\\src\\server\\serial" + "\\" + "file.txt");
                            if (im2.length() > 0) {
                                FileInputStream fileInputStream = new FileInputStream(im2);
                                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                                IDENTY = (HashMap<Integer, String>) objectInputStream.readObject();
                            }
                            try {
                                BY_WHAT = input.readUTF();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            String name_file_2;
                            if (BY_WHAT.equals("BY_ID")) {
                                int id;
                                try {
                                    id = Integer.parseInt(input.readUTF());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                name_file_2 = IDENTY.get(id);
                                IDENTY.remove(id);
                            } else {
                                try {
                                    name_file_2 = input.readUTF();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            File file = new File(DATA_DIR + "\\" + name_file_2);
                            if (file.delete()) {
                                try {
                                    output.writeUTF("200");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                try {
                                    output.writeUTF("404");
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            break;
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
