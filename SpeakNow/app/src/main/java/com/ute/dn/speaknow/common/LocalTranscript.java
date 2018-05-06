package com.ute.dn.speaknow.common;


import com.ute.dn.speaknow.models.Transcript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class LocalTranscript {

    public static List<Transcript> readData(String TranscriptPath) {
        List<Transcript> lstData = new ArrayList<>();
        File file = new File(TranscriptPath);
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
            br.close();
            //Try read data with format 1
            readDataWithFormat1(content, lstData);
        } catch (Exception e) {
            try {
                //Try read data with format 2
                readDataWithFormat2(content, lstData);
            } catch (Exception ex) {
                lstData.clear();
            }
        }
        return lstData;
    }

    private static void readDataWithFormat1(StringBuilder content, List<Transcript> lstData){
        /*
            Format:
            mm:ss transcript 1
            mm:ss transcript 2
            ....................
            mm:ss transcript n
        */
        lstData.clear();
        String[] list = content.toString().split("\n");
        for(int i = 0; i < list.length; i++){
            String[] arr = list[i].split(" ");
            String[] arrTime = arr[0].split(":");
            int start = Integer.parseInt(arrTime[0].trim())*60*1000
                    + Integer.parseInt(arrTime[1].trim())*1000;
            String trans = list[i].substring(arr[0].length()).trim();
            lstData.add(new Transcript(i, trans, start, 0));
        }

        if(lstData.size() == 0) return;

        for (int i = 0; i < lstData.size() - 1; i++){
            lstData.get(i).setDuration(lstData.get(i + 1).getStrart() - lstData.get(i).getStrart());
        }
        lstData.get(lstData.size() - 1).setDuration(Integer.MAX_VALUE);
    }

    private static void readDataWithFormat2(StringBuilder content, List<Transcript> lstData){
        /*
            Format:
            mm:ss
            transcript 1
            mm:ss
            transcript 2
            ....................
            mm:ss
            transcript n
        */
        lstData.clear();
    }
}
