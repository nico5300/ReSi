package me.ocin5300.ReSi.logik;




import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;


public class Registermaschine {
    private SimpleIntegerProperty BZ = new SimpleIntegerProperty(0);

    private SimpleIntegerProperty A = new SimpleIntegerProperty(0);

    private ObservableList<SimpleIntegerProperty> R = FXCollections.observableArrayList();
    private String code;
    private ArrayList<String> anweisungen = null;
    
    private SimpleBooleanProperty ausführendState = new SimpleBooleanProperty(false);
    private Timer timer = null;


    public Registermaschine(int numberOfRegisters) {
        for(int i = 0; i < numberOfRegisters; i++) {
            R.add(new SimpleIntegerProperty(0));
        }
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void reset() {
        for (SimpleIntegerProperty i : R)
            i.set(0);
        A.set(0);
        BZ.set(0);
        ausführendState.set(false);
        anweisungen = null;
    }

    public void prepareAusführung() {
        ausführendState.set(true);
        Scanner sc = new Scanner(code);
        anweisungen = new ArrayList<>(64);

        while (sc.hasNextLine()) {
            anweisungen.add(sc.nextLine());
        }

    }

    public void startTimer(long millis) {
        if(!ausführendState.get())
            return;

        timer = new Timer("runningTimer",true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                anweisungAusführen();
            }
        }, millis, millis);
    }

    public void stopTimer() {
        if(timer != null)
            timer.cancel();
        timer = null;
    }

    public void einzelschritt() {
        if (ausführendState.get())
            anweisungAusführen();
    }

    public void anweisungAusführen() {

        String auszuführendeAnweisung = anweisungen.get(BZ.get()).toUpperCase();
        auszuführendeAnweisung  = auszuführendeAnweisung.trim();

        if(auszuführendeAnweisung.equals("") || auszuführendeAnweisung.substring(0, 2).equals("--")) {
            BZ.set(BZ.get() + 1);
            return;
        }

        if(auszuführendeAnweisung.equals("END")) {
            BZ.set(BZ.get() + 1);
            ausführendState.set(false);
            //TODO: RETURN FINISHED!!!
        }

        Scanner sc = new Scanner(auszuführendeAnweisung);
        String operation = sc.next();
        int arg = sc.nextInt();

        switch (operation) {
            case "NOP":
                BZ.set(BZ.get()+1);
                break;
            case "LOAD":
                A.set(R.get(arg).get());
                BZ.set(BZ.get()+1);
                break;
            case "DLOAD":
                A.set(arg);
                BZ.set(BZ.get()+1);
                break;
            case "STORE":
                R.get(arg).set(A.get());
                BZ.set(BZ.get()+1);
                break;
            case "ADD":
                A.set(A.get() + R.get(arg).get());
                BZ.set(BZ.get()+1);
                break;
            case "SUB":
                A.set(A.get() - R.get(arg).get());
                BZ.set(BZ.get()+1);
                break;
            case "MULT":
                A.set(A.get() * R.get(arg).get());
                BZ.set(BZ.get()+1);
                break;
            case "DIV":
                A.set(A.get() / R.get(arg).get());
                BZ.set(BZ.get()+1);
                break;
            case "MOD":
                A.set(A.get() % R.get(arg).get());
                BZ.set(BZ.get()+1);
                break;
            case "JUMP":
                BZ.set(arg);
                break;
            case "JGE":
                if(A.get() >= 0)
                    BZ.set(arg);
                else
                    BZ.set(BZ.get()+1);
                break;
            case "JGT":
                if( A.get() > 0)
                    BZ.set(arg);
                else
                    BZ.set(BZ.get()+1);
                break;
            case "JLE":
                if (A.get() <= 0)
                    BZ.set(arg);
                else
                    BZ.set(BZ.get()+1);
                break;
            case "JLT":
                if (A.get() < 0)
                    BZ.set(arg);
                else
                    BZ.set(BZ.get()+1);
                break;
            case "JEQ":
                if (A.get() == 0)
                    BZ.set(arg);
                else
                    BZ.set(BZ.get()+1);
                break;
            case "JNE":
                if (A.get() != 0)
                    BZ.set(arg);
                else
                    BZ.set(BZ.get()+1);
                break;
            case "END":
                BZ.set(BZ.get()+1);
                ausführendState.set(false);
                break;
        }
    }


    public ObservableList<SimpleIntegerProperty> getR() {
        return R;
    }

}
