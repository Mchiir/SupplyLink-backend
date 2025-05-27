package com.supplylink;

import java.util.HashMap;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Test {
    public static void main(String[] args) {
        Map<String,Integer>ages=new HashMap<String,Integer>();
        ages.put("mike",18);
        ages.put("michael",17);
        ages.put("gael",20);

        Map<String,String> userDetails=new HashMap<String,String>();
        userDetails.put("username","jovin");
        userDetails.put("email","jovin@gmail.com");
        userDetails.put("group","y2d");
    }
}
