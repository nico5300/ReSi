package me.ocin5300.ReSi.logik;


import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;


public class Registermaschine {
    private SimpleIntegerProperty BZ = new SimpleIntegerProperty(0);

    private SimpleIntegerProperty A = new SimpleIntegerProperty(0);

    private SimpleIntegerProperty[] R;
    private String code;
    private ArrayList<String> anweisungen = null;
    
    private SimpleBooleanProperty ausführendState = new SimpleBooleanProperty(false);
    private Timer timer = null;

    RegistermaschinenListener listener;


    public Registermaschine(RegistermaschinenListener listener, int numberOfRegisters) {
        this.listener = listener;
        R = new SimpleIntegerProperty[numberOfRegisters];
        for(int i = 0; i < numberOfRegisters; i++) {
            R[i] = new SimpleIntegerProperty(0);
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
        listener.updateHighlighting(-1);
        ausführendState.set(false);
        anweisungen = null;
    }

    public void prepareAusführung() {
        ausführendState.set(true);
        Scanner sc = new Scanner(code);
        anweisungen = new ArrayList<>(64);
        listener.updateHighlighting(BZ.get());

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

        String auszuführendeAnweisung;
        try {
            auszuführendeAnweisung = anweisungen.get(BZ.get()).toUpperCase();
        } catch (IndexOutOfBoundsException e) {
            listener.errorEncountered("FEHLER: Unerwartetes Programmende! You made a CPU cry! Shame on you!", BZ.get());
            ausführendState.set(false);
            stopTimer();
            return;
        }
        auszuführendeAnweisung  = auszuführendeAnweisung.trim();

        if(auszuführendeAnweisung.equals("") || auszuführendeAnweisung.substring(0, 2).equals("--")) {
            BZ.set(BZ.get() + 1);
            return;
        }

        if(auszuführendeAnweisung.startsWith("END")) {
            System.out.println("------END ENCOUNTERED!!!");
            BZ.set(BZ.get() + 1);
            stopTimer();
            ausführendState.set(false);
            listener.endEncountered();
            return;
        }

        if(auszuführendeAnweisung.startsWith("NOP")) {
            BZ.set(BZ.get()+1);
        }


        Scanner sc = new Scanner(auszuführendeAnweisung);
        String operation = sc.next();
        int arg = -1;
        if(sc.hasNextInt())
            arg = sc.nextInt();
        else {
            listener.errorEncountered("FEHLER: In Zeile " + BZ.get() + " fehlt der Integer " +
                    "nach der Anweisung. ", BZ.get());
            ausführendState.set(false);
            stopTimer();
        }

        switch (operation) {
            case "LOAD":
                A.set(R[arg-1].get());
                BZ.set(BZ.get()+1);
                break;
            case "DLOAD":
                A.set(arg);
                BZ.set(BZ.get()+1);
                break;
            case "STORE":
                R[arg-1].set(A.get());
                BZ.set(BZ.get()+1);
                break;
            case "ADD":
                A.set(A.get() + R[arg-1].get());
                BZ.set(BZ.get()+1);
                break;
            case "SUB":
                A.set(A.get() - R[arg-1].get());
                BZ.set(BZ.get()+1);
                break;
            case "MULT":
                A.set(A.get() * R[arg-1].get());
                BZ.set(BZ.get()+1);
                break;
            case "DIV":
                A.set(A.get() / R[arg-1].get());
                BZ.set(BZ.get()+1);
                break;
            case "MOD":
                A.set(A.get() % R[arg-1].get());
                BZ.set(BZ.get()+1);
                break;
            case "JUMP":
                BZ.set(arg-1);
                break;
            case "JGE":
                if(A.get() >= 0)
                    BZ.set(arg-1);
                else
                    BZ.set(BZ.get()+1);
                break;
            case "JGT":
                if( A.get() > 0)
                    BZ.set(arg-1);
                else
                    BZ.set(BZ.get()+1);
                break;
            case "JLE":
                if (A.get() <= 0)
                    BZ.set(arg-1);
                else
                    BZ.set(BZ.get()+1);
                break;
            case "JLT":
                if (A.get() < 0)
                    BZ.set(arg-1);
                else
                    BZ.set(BZ.get()+1);
                break;
            case "JEQ":
                if (A.get() == 0)
                    BZ.set(arg-1);
                else
                    BZ.set(BZ.get()+1);
                break;
            case "JNE":
                if (A.get() != 0)
                    BZ.set(arg-1);
                else
                    BZ.set(BZ.get()+1);
                break;
             default:

                 listener.errorEncountered("FEHLER: In Zeile " + (BZ.get()+1) + ": Unbekanntes Symbol", BZ.get());
                 ausführendState.set(false);
                 stopTimer();
                 return;
        }
        Platform.runLater(() -> listener.updateHighlighting(BZ.get()));
    }


    public SimpleIntegerProperty[] getR() {
        return R;
    }

    public int getBZ() {
        return BZ.get();
    }

    public SimpleIntegerProperty BZProperty() {
        return BZ;
    }

    public int getA() {
        return A.get();
    }

    public SimpleIntegerProperty aProperty() {
        return A;
    }


}
