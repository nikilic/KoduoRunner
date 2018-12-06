package com.prvaci.koduorunner;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String appcode =
                    "app proba\n" +
                    "name Proba\n" +
                    "author nikilic\n" +
                    "main probabutton\n" +
                    "act probabutton\n" +
                    "__background__ set __black__\n" +
                    "add text \"Klikni dugme\",__white__,16\n" +
                    "add button \"Klikni me!\",klik\n" +
                    ";" +
                    "fun klik" +
                    "message \"Zdravo\"" +
                    ";";

    boolean flag;
    Map<String, String> SysVar, UserVar, Acts, Funs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String data, line, tempname;

        SysVar = new HashMap<>();
        UserVar = new HashMap<>();
        Acts = new HashMap<>();
        Funs = new HashMap<>();
        SysVar.put("app", "");
        SysVar.put("name", "");
        SysVar.put("author", "");
        SysVar.put("main", "");
        SysVar.put("__black__", "#000000");
        SysVar.put("__white__", "#ffffff");

        //GENERAL INFO
        BufferedReader appReader = new BufferedReader(new StringReader(appcode));
        for(int i = 0; i < 4; i++) {
            String lineSplit[] = lineReader(appReader).split(" ");
            if(lineSplit.length != 1) {
                data = "";
                for (int j = 1; j < lineSplit.length - 1; j++) {
                    data += lineSplit[1] + " ";
                }
                data += lineSplit[lineSplit.length - 1];
                if (lineSplit[0].equals("app") || lineSplit[0].equals("name") || lineSplit[0].equals("author") || lineSplit[0].equals("main")) {
                    SysVar.put(lineSplit[0], data);
                }
            }
        }

        line = lineReader(appReader);
        while(!line.equals("error")){
            //ACT CODE
            if(line.split(" ")[0].equals("act")){
                tempname = line.split(" ")[1];
                Acts.put(tempname,"");
                while(!line.equals(";")){
                    Acts.put(tempname,Acts.get(tempname)+line+"\"");
                }
            }
            //FUN CODE
            if(line.split(" ")[0].equals("fun")){
                tempname = line.split(" ")[1];
                Funs.put(tempname,"");
                while(!line.equals(";")){
                    Funs.put(tempname,Funs.get(tempname)+line+"\"");
                }
            }
            //SET CODE
            if(spaceCount(line) > 2 && line.split(" ")[1].equals("set")){
                UserVar.put(line.split(" ")[0],getValue(line,2));
            }
        }

        LinearLayout contentView = createContentView(SysVar.get("main"), getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public LinearLayout createContentView(String act, Context context){
        String actcode = Acts.get(act);
        BufferedReader actReader = new BufferedReader(new StringReader(actcode));
        LinearLayout parent = new LinearLayout(context);
        String line = lineReader(actReader);
        while(!line.equals("error")){
            //GENERAL INFO CODE
            if(line.split(" ")[0].equals("background")){
                parent.setBackgroundColor(Color.parseColor(getValue(line,1)));
            }
            //SET CODE
            if(spaceCount(line) > 2 && line.split(" ")[1].equals("set")){
                UserVar.put(line.split(" ")[0],getValue(line,2));
            }
            //ADD CODE
            if(line.split(" ")[0].equals("add")){
                String[] params = line.split(" ")[2].split(",");
                
            }
        }
        return parent;
    }

    public String getValue(String line, int use){
        String[] lineSplit = line.split(" ");
        //USE: 1 - DATA AFTER 1 SPACE, 2 - DATA AFTER 2 SPACES
        if (lineSplit[use].charAt(0) == '\"') {
            return line.split("\"")[1];
        } else if (Character.isDigit(lineSplit[2].charAt(0))) {
            return lineSplit[use].replaceAll("[^\\d.]", "");
        } else {
            if (("" + lineSplit[use].charAt(0) + lineSplit[use].charAt(1)).equals("__")) {
                return SysVar.get(lineSplit[use]);
            } else {
                return UserVar.get(lineSplit[use]);
            }
        }
    }

    static String lineReader(BufferedReader reader){
        String line;
        try {
            while(true) {
                line = reader.readLine();
                if (line != null && line.replaceAll("\\s", "").equals("")) {
                    return line;
                }
            }
        } catch(IOException e) {
            Log.e("IO Exception","ERROR 1 - IO EXCEPTION");
        }
        return "error";
    }

    static int spaceCount(String line){
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                count++;
            }
        }
        return count;
    }
}
