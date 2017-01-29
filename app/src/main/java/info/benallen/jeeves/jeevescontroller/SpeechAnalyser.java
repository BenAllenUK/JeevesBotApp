package info.benallen.jeeves.jeevescontroller;

import java.util.ArrayList;

/**
 * Created by maartie0 on 29/01/2017.
 */

public class SpeechAnalyser {

    private ArrayList<Enum> mCommands = new ArrayList<>();

    public enum Subject {
        MARTIN , BEN, RORY,
        LOGAN, MIKE
    }


    public enum Entertain {
        DANCE
    }


    public ArrayList<Enum> getmCommands() {
        return mCommands;
    }

    public int setCommand(ArrayList<String> data) {
        ArrayList<Enum> commands = findCommands(data);
        if (isAccepted(commands)) {
            setField(commands);
            return 0;
        } else if (commands.contains(Entertain.DANCE)) {
            return 1;
        } else {
            return 2;
        }
    }

    private void setField(ArrayList<Enum> commands) {
        ArrayList<Enum> temp = new ArrayList<>(commands);
        for(Enum command : commands){
            temp = trimCommands(temp,command);
        }
        temp = sortEnumArray(temp);
        mCommands = temp;
    }

    private ArrayList<Enum> trimCommands(ArrayList<Enum> commands, Enum original ){
        ArrayList<Enum> temp = new ArrayList(commands);
        for (Enum command : commands){
            if(command.getDeclaringClass() == original.getDeclaringClass()){
                temp.remove(command);
            }
        }
        temp.add(original);
        return temp;
    }


    private boolean isAccepted(ArrayList<Enum> commands) {
        boolean subject = false;
        boolean magicWord = false;
        for (Enum command: commands){
            if(command instanceof Subject){
                subject = true;
            }
        }
        return subject;
    }


    private ArrayList<Enum> findCommands(ArrayList<String> data){
        ArrayList<Enum> commands = new ArrayList<>();
        for (String command : data){
            String brokenString[] = command.split(" ");
            for (String word : brokenString) {
                word = word.toUpperCase();
                addSubject(commands,word);
                if("DANCE".equals(word) || "DANSE".equals(word) || "BOUNCE".equals(word) || "LANCE".equals(word) || "CHANCE".equals(word) || "TRANS".equals(word)){
                    commands.add(Entertain.DANCE);
                }
            }
        }
        return commands;
    }

    private void addSubject(ArrayList<Enum> commands,String word){
        if ("MARTIN".equals(word) || "MARTIN".equals(word) || "BARTON".equals(word) || "MARTEN".equals(word)) {
            commands.add(Subject.MARTIN);
        } else if ("BEN".equals(word) || "BEEN".equals(word) || "10".equals(word) || "BEM".equals(word)) {
            commands.add(Subject.BEN);
        } else if ("WORRY".equals(word) || "RORY".equals(word) || "MURRAY".equals(word) || "OI".equals(word)) {
            commands.add(Subject.RORY);
        } else if ("LOGAN".equals(word) || "OLLY".equals(word) || "POLLY".equals(word) || "BULLY".equals(word) || "BALLE".equals(word) || "HOLLY".equals(word) || "POLI".equals(word)) {
            commands.add(Subject.LOGAN);
        }  else if ("MIKE".equals(word) || "MIC".equals(word) || "LIKE".equals(word) || "MY".equals(word)) {
            commands.add(Subject.MIKE);
        }
    }

    private ArrayList<Enum> sortEnumArray(ArrayList<Enum> commands){
        ArrayList<Enum> temp = new ArrayList<>(commands);
        for (Enum command : commands){
            if (command instanceof Subject){
                temp.remove(command);
                temp.add(0,command);
            }
        }
        return temp;
    }
}
