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

    private void real_run() {
        try {
            socket = AsynchronousSocketChannel.open();
            socket.connect(new InetSocketAddress(ServiceSocket.hostname, ServiceSocket.Port)).get();

            buffer = ByteBuffer.allocate(BUFFER_SIZE);
            charset = Charset.forName("UTF-8");


            Log.d("API","socket created");

            socket.read(buffer, null, new CompletionHandler<Integer, Void>() {
                @Override
                public void completed(Integer result, Void attachment) {
                    if (result == -1) {
                        close_all(); //
                    }

                    buffer.flip();
                    String message = charset.decode(buffer).toString();
                    if (message == null)
                        message = "";

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

        } catch (Exception e) {
            e.printStackTrace();
            close_all();
        }
    }

    public void send_message(String msg) {
        if (msg != null && !msg.isEmpty()) {
            new Sender().execute(msg);
        }
    }

    public void send_messages(String... msg) {
         new Sender().execute(msg);
    }

    public void close_all() {
        try {
            Log.d("API", "Stopping ClientThread");
            if (socket != null) {
                socket.close();
            }
            if (outputWriter != null) {
                outputWriter.close();
            }
            if (inputReader != null) {
                inputReader.close();
            }
            this.stop();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void onGet_message_from_server(String msg);

    private class Getter extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            real_run();
            return null;
        }
    }

    private class Sender extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String res = "";
                try {
                    for (String param : params) {

                        if (param.equals(""))
                            param = " ";

                        byte[] bytes = param.getBytes(charset);
                        ByteBuffer buffer = ByteBuffer.wrap(bytes);
                        socket.write(buffer).get();
                        sleep(100);
                    }
                    return res;
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}