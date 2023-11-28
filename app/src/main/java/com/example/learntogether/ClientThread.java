package com.example.learntogether;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;


public abstract class ClientThread extends Thread {
    /*
    private Socket socket;*/
    private PrintWriter outputWriter;
    private BufferedReader inputReader;

    private AsynchronousSocketChannel socket;
    private ByteBuffer buffer;
    private Charset charset;
    private static final int BUFFER_SIZE = 2048;


    @Override
    public void run() {
        new Getter().execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void real_run() {
        try {
            /*
            socket = new Socket("192.168.3.73", 9540); // Replace with your server IP and port
            outputWriter = new PrintWriter(socket.getOutputStream(), true);
            inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             */

            socket = AsynchronousSocketChannel.open();
            socket.connect(new InetSocketAddress("192.168.3.73", 9540)).get();

            buffer = ByteBuffer.allocate(BUFFER_SIZE);
            charset = Charset.forName("UTF-8");


            Log.d("API","socket created");

            socket.read(buffer, null, new CompletionHandler<Integer, Void>() {
                @Override
                public void completed(Integer result, Void attachment) {
                    if (result == -1) {
                        close_all();
                    }

                    buffer.flip();
                    String message = charset.decode(buffer).toString();
                    buffer.clear();

                    Log.d("API", "onGet from server");
                    onGet_message_from_server(message);

                    socket.read(buffer, null, this);
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    exc.printStackTrace();
                    close_all();
                }

            });
/*
            new Thread(() -> {
                while (true) {
                    try {
                        String message;
                        while ((message = inputReader.readLine()) != null) {
                            Log.d("API","onGet from server");
                            onGet_message_from_server(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();*/

        } catch (Exception e) {
            e.printStackTrace();
            close_all();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void send_message(String msg) {
        /*
        if (msg != null && !msg.isEmpty()) {
        try {
                outputWriter.println(msg);
            } catch (Exception e) {
            e.printStackTrace();
        }
        }

         */


        if (msg != null && !msg.isEmpty()) {
            new Sender().execute(msg);
        }
    }

    public void close_all() {
        try {
            if (socket != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    socket.close();
                }
            }
            if (outputWriter != null) {
                outputWriter.close();
            }
            if (inputReader != null) {
                inputReader.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void onGet_message_from_server(String msg);

    private class Getter extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                real_run();
            }
            return null;
        }
    }

    private class Sender extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    byte[] bytes = params[0].getBytes(charset);
                    ByteBuffer buffer = ByteBuffer.wrap(bytes);
                    socket.write(buffer).get();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}