package br.com.renanxavier.sd01;


import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class SendData extends AsyncTask<List, Void, Void>{

    Socket s;
    ObjectOutputStream out;
    String ipServer="";

    public void setIp(String ip){
        this.ipServer = ip;
    }

    @Override
    protected Void doInBackground(List... lists) {

        List<String> list = lists[0];

        try {
            s = new Socket(ipServer, 4800);
            out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(list);
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
