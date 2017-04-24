package com.minelife.util;

import com.google.common.collect.Lists;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonFile {

    public final File file;

    public JsonFile(File file) {
        this.file = file;
    }

    public JSONObject read() {
        JSONParser parser = new JSONParser();
        try {
            FileReader fileReader = new FileReader(file);
            return (JSONObject) parser.parse(fileReader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void write(JSONObject object) {
        try {
            List<String> toWrite = Lists.newArrayList();

            if (object == null)
                toWrite.add("null");
            else {
                boolean var2 = true;
                Iterator var3 = object.entrySet().iterator();
                toWrite.add("{");

                while (var3.hasNext()) {
                    if (var2)
                        var2 = false;
                    else
                        toWrite.add(",");

                    Map.Entry var4 = (Map.Entry) var3.next();

                    StringBuffer var1 = new StringBuffer();
                    toJSONString(String.valueOf(var4.getKey()), var4.getValue(), var1);
                    toWrite.add(var1.toString());
                }

                toWrite.add("}");
            }

            Files.write(file.toPath(), toWrite, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toJSONString(String var0, Object var1, StringBuffer var2) {
        var2.append('\"');
        if (var0 == null)
            var2.append("null");
        else
            JSONValue.escape(var0);


        var2.append(var0);
        var2.append('\"').append(':');

        String s = JSONValue.toJSONString(var1);
        String toString = "";
        String tabs = "";
        boolean startWord = false;

        for (int i = 0; i < s.toCharArray().length; i++) {
            char c = s.toCharArray()[i];

            if (c == '{') {
                toString += tabs + c + System.lineSeparator();
                tabs += '\t';
                startWord = false;
            } else if (c == '[') {
                tabs += '\t';
                toString += c + System.lineSeparator();
                startWord = false;
            } else if (c == ',') {
                toString += c + System.lineSeparator();
                startWord = false;
            } else if (c == '}' || c == ']') {
                if (!tabs.isEmpty()) tabs = tabs.substring(0, tabs.length() - 1);
                toString += System.lineSeparator() + tabs + c;
            } else {
                if (!startWord) {
                    toString += tabs + c;
                    startWord = true;
                } else {
                    toString += c;
                }
            }
        }

        var2.append(toString);

        return var2.toString();
    }

}
