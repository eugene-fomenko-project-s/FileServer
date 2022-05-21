package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;


enum state {
    GET,
    PUT,
    DELETE
}

public class client extends Thread {
    state state_z;

    public void start() {
        System.out.println("Enter action (1 - get a file, 2 - create a file, 3 - delete a file):");
        try (
                Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 12123);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            while (true) {
                Scanner sc = new Scanner(System.in);
                String take = sc.nextLine();
                if (!take.equals("exit")) {
                    switch (take) {
                        case "1":
                            System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id):");
                            output.writeUTF("GET");
                            String ID_OR_NAME = sc.nextLine();
                            output.writeUTF(ID_OR_NAME.equals("1") ? "BY_NAME" : "BY_ID");
                            System.out.println(ID_OR_NAME.equals("1") ? "Enter name:" : "Enter id:");
                            String name = sc.nextLine();
                            output.writeUTF(name);
                            state_z = state.GET;
                            break;
                        case "2":
                            System.out.println("Enter name of the file:");
                            String f = sc.nextLine();
                            output.writeUTF("PUT");
                            System.out.println("Enter name of the file to be saved on server:");
                            String p = sc.nextLine();
                            if(p.isEmpty()){
                                p = "none";
                            }
                            output.writeUTF(p);
                            File file = new File("C:\\Users\\fomen\\IdeaProjects\\Car Sharing\\Car Sharing\\File Server\\File Server\\task\\src\\client\\data\\" + f);
                            byte[] array;
                            if (!file.exists()) {
                                System.out.println("Enter the file content:");
                                String g = sc.nextLine();
                                try {
                                    FileWriter writer = new FileWriter(file);
                                    writer.write(g);
                                    writer.close();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            array = Files.readAllBytes(Paths.get(String.valueOf(file)));
                            output.writeInt(array.length);
                            output.write(array);
                            state_z = state.PUT;
                            break;
                        case "3":
                            System.out.println("Do you want to delete the file by name or by id (1 - name, 2 - id):");
                            String ID_Or_NAME = sc.nextLine();
                            output.writeUTF("DELETE");
                            output.writeUTF(ID_Or_NAME.equals("1") ? "BY_NAME" : "BY_ID");
                            System.out.println(ID_Or_NAME.equals("1") ? "Enter name:" : "Enter id:");
                            String name_delete = sc.nextLine();
                            output.writeUTF(name_delete);
                            state_z = state.DELETE;
                            break;
                        default:
                            output.writeUTF("EXIT");
                            socket.close();
                            System.exit(0);
                            break;
                    }
                    System.out.println("The request was sent.");
                    String a = input.readUTF();
                    switch (a) {
                        case "200":
                            switch (state_z) {
                                case GET:
                                    System.out.println("The file was downloaded! Specify a name for it:");
                                    String name_saved = sc.nextLine();
                                    int length = input.readInt();
                                    byte[] array = new byte[length];
                                    input.readFully(array, 0, length);
                                    File file = new File("C:\\Users\\fomen\\IdeaProjects\\Car Sharing\\Car Sharing\\File Server\\File Server\\task\\src\\client\\data\\" + name_saved);
                                    try {
                                        if (file.createNewFile()) {
                                            OutputStream outStream = new FileOutputStream(file);
                                            outStream.write(array);
                                            outStream.close();
                                        }
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    System.out.println("File saved on the hard drive! \n");
                                    break;
                                case PUT:
                                    System.out.printf("Response says that file is saved! ID = %d \n", input.readInt());
                                    break;
                                case DELETE:
                                    System.out.println("The response says that this file was deleted successfully! \n");
                            }
                            break;
                        case "404":
                            switch (state_z) {
                                case GET:
                                case DELETE:
                                    System.out.println("The response says that this file is not found!\n");
                                    break;
                            }
                            return;
                        case "403":
                            if (state_z == state.PUT) {
                                System.out.println("The response says that creating the file was forbidden! \n");
                            }
                            return;
                    }
                } else {
                    output.writeUTF("EXIT");
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}