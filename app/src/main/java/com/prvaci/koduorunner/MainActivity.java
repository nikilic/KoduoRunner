package com.prvaci.koduorunner;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /*String appcode =
                    "app proba\n" +
                    "name Proba\n" +
                    "author nikilic\n" +
                    "main probabutton\n" +
                    "texttext set \"Klikni dugme\"\n" +
                    "act probabutton\n" +
                    "add text \"ZDRAVO!!!\",__black__,24\n" +
                    "add text texttext,__black__,16\n" +
                    "add button \"Klikni me!\",klik\n" +
                    ";\n" +
                    "fun klik\n" +
                    "message \"Zdravo\"\n" +
                    ";";*/
    String appcode = "app proba\n" +
            "name Proba\n" +
            "author nikilic\n" +
            "main probabutton\n" +
            "texttext set \"Klikni dugme\"\n" +
            "var1 set \"proba\"\n" +
            "var2 set var1\n" +
            "var3 set 10\n" +
            "num var4 set 10\n" +
            "num var5 set var4\n" +
            "act probabutton\n" +
            "add text \"ZDRAVO!!!\",__black__,24\n" +
            "add text texttext,__black__,16\n" +
            "add button \"Klikni me!\",klik\n";

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
        Log.i("KDINFO","START");
        BufferedReader appReader = new BufferedReader(new StringReader(appcode));
        Log.i("KDINFO","READER");
        for(int i = 0; i < 4; i++) {
            String lineSplit[] = lineReader(appReader).split(" ");
            if (lineSplit.length != 1) {
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
        Log.i("KDINFO","ADDED GENERAL INFO");

        line = lineReader(appReader);
        while(!line.equals("error")){
            Log.i("KDINFO","NEW ITERATION");
            //ACT CODE
            if(line.split(" ")[0].equals("act")){
                tempname = line.split(" ")[1];
                Acts.put(tempname,"");
                while(!line.equals(";")){
                    Acts.put(tempname,Acts.get(tempname)+line+"\n");
                    line = lineReader(appReader);
                }
                Log.i("KDINFO","ADDED ACT " + tempname);
            }
            //FUN CODE
            else if(line.split(" ")[0].equals("fun")){
                tempname = line.split(" ")[1];
                Funs.put(tempname,"");
                while(!line.equals(";")){
                    Funs.put(tempname,Funs.get(tempname)+line+"\n");
                    line = lineReader(appReader);
                }
                Log.i("KDINFO","ADDED FUN " + tempname);
            }
            //SET CODE
            else if(spaceCount(line) > 2 && line.split(" ")[1].equals("set")){
                UserVar.put(line.split(" ")[0],getValue(line,2));
                Log.i("KDINFO","CHANGED " + line.split(" ")[0]);
            }
            //NUM CODE
            else if(line.split(" ")[0].equals("num")){
                switch (line.split(" ")[2]){
                    case "set":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0))));
                        break;
                    case "plus":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) + Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                    case "minus":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) - Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                    case "times":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) * Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                    case "divide":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) / Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                    case "mod":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) % Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                }
            }
            line = lineReader(appReader);
        }

        Log.i("KDINFO", "problem???");
        Log.i("VAROVI",UserVar.get("var1"));
        Log.i("VAROVI",UserVar.get("var2"));
        Log.i("VAROVI",UserVar.get("var3"));
        Log.i("VAROVI",UserVar.get("var4"));
        Log.i("VAROVI",UserVar.get("var5"));
        LinearLayout contentView = createContentView(SysVar.get("main"), getApplicationContext());
        LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        super.onCreate(savedInstanceState);
        setContentView(contentView,linLayoutParam);
    }

    public LinearLayout createContentView(String act, final Context context){
        Log.i("KDINFO", "createContentView");
        String actcode = Acts.get(act);
        BufferedReader actReader = new BufferedReader(new StringReader(actcode));
        Log.i("KDINFO", "actReader");
        LinearLayout parent = new LinearLayout(context);
        parent.setOrientation(LinearLayout.VERTICAL);
        Log.i("KDINFO", "LinearLayout");
        String line = lineReader(actReader);
        while(!line.equals("error")){
            //SET CODE
            if(spaceCount(line) > 2 && line.split(" ")[1].equals("set")){
                UserVar.put(line.split(" ")[0],getValue(line,2));
            }
            //NUM CODE
            else if(line.split(" ")[0].equals("num")){
                switch (line.split(" ")[2]){
                    case "set":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0))));
                        break;
                    case "plus":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) + Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                    case "minus":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) - Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                    case "times":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) * Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                    case "divide":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) / Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                    case "mod":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) % Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                }
            }
            //ADD CODE
            else if(line.split(" ")[0].equals("add")){
                switch (line.split(" ")[1]){
                    case "text":
                        String[] paramstext = line.substring(9, line.length()).split(",");
                        Log.i("KDPARAMS",paramstext[0]);
                        Log.i("KDPARAMS",paramstext[1]);
                        Log.i("KDPARAMS",paramstext[2]);
                        TextView textView = new TextView(context);
                        textView.setText(getValue(paramstext[0],0));
                        textView.setTextColor(Color.parseColor(getValue(paramstext[1],0)));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.valueOf(getValue(paramstext[2],0)));
                        LinearLayout.LayoutParams layoutparamstext = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutparamstext.setMargins(32, 32, 32, 0);
                        layoutparamstext.gravity = Gravity.CENTER;
                        textView.setLayoutParams(layoutparamstext);
                        parent.addView(textView);
                        break;
                    case "button":
                        final String[] paramsbutton = line.substring(11, line.length()).split(",");
                        Button button = new Button(context);
                        button.setText(getValue(paramsbutton[0],0));
                        button.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Log.i("KDFUN","START");
                                funrun(paramsbutton[1],context);
                            }
                        });
                        LinearLayout.LayoutParams layoutparamsbutton = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutparamsbutton.setMargins(32, 32, 32, 0);
                        layoutparamsbutton.gravity = Gravity.CENTER;
                        button.setLayoutParams(layoutparamsbutton);
                        button.setGravity(Gravity.CENTER);
                        parent.addView(button);
                        break;
                }
            }
            line = lineReader(actReader);
        }
        return parent;
    }

    public void funrun(String fun, Context context){
        Log.i("KDFUN","FUNRUN " + fun);
        String funcode = Funs.get(fun);
        Log.i("KDFUN",funcode);
        BufferedReader funReader = new BufferedReader(new StringReader(funcode));
        String line = lineReader(funReader);
        while(!line.equals("error")){
            //SET CODE
            if(spaceCount(line) > 2 && line.split(" ")[1].equals("set")){
                UserVar.put(line.split(" ")[0],getValue(line,2));
            }
            //NUM CODE
            else if(line.split(" ")[0].equals("num")){
                switch (line.split(" ")[2]){
                    case "set":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0))));
                        break;
                    case "plus":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) + Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                    case "minus":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) - Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                    case "times":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) * Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                    case "divide":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) / Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                    case "mod":
                        UserVar.put(line.split(" ")[1],String.valueOf(Integer.parseInt(getValue(line.split(" ")[3],0)) % Integer.parseInt(UserVar.get(getValue(line.split(" ")[1],0)))));
                        break;
                }
            }
            //MESSAGE CODE
            else if(line.split(" ")[0].equals("message")){
                String[] paramsmessage = line.substring(8, line.length()).split(",");
                Toast.makeText(context, getValue(paramsmessage[0],0), Toast.LENGTH_LONG).show();
            }
            line = lineReader(funReader);
        }
    }

    public String getValue(String line, int use){
        String[] lineSplit = line.split(" ");
        //USE: 1 - DATA AFTER 1 SPACE, 2 - DATA AFTER 2 SPACES
        if (lineSplit[use].charAt(0) == '\"') {
            return line.split("\"")[1];
        } else if (Character.isDigit(lineSplit[use].charAt(0))) {
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
                if (line != null){ //&& line.replaceAll("\\s", "").equals("")) {
                    Log.i("KDLINE",line);
                    return line;
                }
                return "error";
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
