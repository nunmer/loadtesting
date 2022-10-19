package org.example;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Processor of HTTP request.
 */
public class Processor {
    private final Socket socket;
    private final HttpRequest request;

    public Processor(Socket socket, HttpRequest request) {
        this.socket = socket;
        this.request = request;
    }

    public String prime(int num){
        if(isPrime(num)){
            return num+" ";
        }
        return null;
    }

    private boolean isPrime(int n) {
        if (n <= 1)
            return false;

        // Check from 2 to n-1
        for (int i = 2; i < n; i++)
            if (n % i == 0)
                return false;

        return true;
    }

    public boolean isEven(int n) {
        if (n%2 == 0){
            return true;
        }else{
            return false;
        }
    }

    public String createFile() {
        String fileName = request.getRequestLine().replaceAll("GET /create/", "");
        fileName = fileName.replaceAll(" HTTP/1.1", "");

        // create a file object for the current location
        File file = new File("/Users/1/Distributing computing/dc-course/examples/WebServer/src/main/java/org/example/"+ fileName + ".txt");


        try {
            boolean value = file.createNewFile();
            if (value) {
                return("New "+ fileName + ".txt file is created: /Users/1/Distributing computing/dc-course/examples/WebServer/src/main/java/org/example/" + fileName);
            }
            else {
                return ("The file already exists.");
            }
        }
        catch(Exception e) {
            e.getStackTrace();
        }

        return fileName;
    }

    public String deleteFile() {
        String fileName = request.getRequestLine().replaceAll("GET /delete/", "");
        fileName = fileName.replaceAll(" HTTP/1.1", "");


        File myObj = new File("/Users/1/Distributing computing/dc-course/examples/WebServer/src/main/java/org/example/"+ fileName + ".txt");

        if (myObj.delete()) {
            return "Successfully deleted the file: " + myObj.getName();
        } else{
            return "Failed to delete the file.";
        }
    }

    public void process() throws IOException {
        // Print request that we received.
        System.out.println("Got request:");
        System.out.println(request.toString());
        System.out.flush();

        // To send response back to the client.
        PrintWriter output = new PrintWriter(socket.getOutputStream());

        if(request.getRequestLine().contains("GET /exec/")) {
            String num = request.getRequestLine().replaceAll("GET /exec/", "");
            num = num.replaceAll(" HTTP/1.1", "");

            int n = Integer.parseInt(num);

            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: text/html; charset=utf-8");
            output.println();
            output.println("<html>");
            output.println("<head><title>Prime numbers</title></head>");
            output.print("<body><p>Prime numbers until " + n + ": ");
            for(int i=2; i<=n; i++){
                if(prime(i)==null){
                    continue;
                }else {
                    output.println(prime(i));
                }
            }
            output.print( "</p></body>");
            output.println("</html>");
            output.flush();
            socket.close();
        }
        else if (request.getRequestLine().contains("GET /create/")) {
            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: text/html; charset=utf-8");
            output.println();
            output.println("<html>");
            output.println("<head><title>Create txt file</title></head>");
            output.println("<body><p>File Created</p></body>");
            output.println("<body><p>" + createFile() +"</p></body>");
            output.println("</html>");
            output.flush();
            socket.close();
        }
        else if (request.getRequestLine().contains("GET /delete/")) {
            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: text/html; charset=utf-8");
            output.println();
            output.println("<html>");
            output.println("<head><title>DELETE</title></head>");
            output.println("<body><p>Delete File</p></body>");
            output.println("<body><p>" + deleteFile() +"</p></body>");
            output.println("</html>");
            output.flush();
            socket.close();
        }

        else {
            // We are returning a simple web page now.
            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: text/html; charset=utf-8");
            output.println();
            output.println("<html>");
            output.println("<head><title>Hello</title></head>");
            output.println("<body><p>Hello, world!</p></body>");
            output.println("</html>");
            output.flush();

            socket.close();
        }
    }
}
