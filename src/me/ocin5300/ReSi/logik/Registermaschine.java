package me.ocin5300.ReSi.logik;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;

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
    private SimpleIntegerProperty tickTime = new SimpleIntegerProperty(1000);
    private SimpleBooleanProperty isTimed = new SimpleBooleanProperty(false);

    private RegistermaschinenListener listener;


    public Registermaschine(RegistermaschinenListener listener, int numberOfRegisters) {
        this.listener = listener;
        R = new SimpleIntegerProperty[numberOfRegisters];
        for(int i = 0; i < numberOfRegisters; i++) {
            R[i] = new SimpleIntegerProperty(0);
        }



        isTimed.addListener(this::determineAutoTick);

        ausführendState.addListener(this::determineAutoTick);

    }


    private void determineAutoTick(ObservableValue<? extends Boolean> observableValue, Boolean oldVal, Boolean newVal) {
        if (isTimed.get() & ausführendState.get()) {
            timer = new Timer("runningTimer", true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (ausführendState.get())
                        anweisungAusführen();
                }
            }, tickTime.get(), tickTime.get());


        } else {
            if (timer != null)
                timer.cancel();
            timer = null;
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
        Scanner sc = new Scanner(code);
        anweisungen = new ArrayList<>(64);
        listener.updateHighlighting(BZ.get());

        while (sc.hasNextLine()) {
            anweisungen.add(sc.nextLine());
        }
        ausführendState.set(true);

    }




    public SimpleIntegerProperty tickTimeProperty() {
        return tickTime;
    }
    

    public void einzelschritt() {
        if (ausführendState.get())
            anweisungAusführen();
    }





    protected void anweisungAusführen() {

        String auszuführendeAnweisung;


        try {
            auszuführendeAnweisung = anweisungen.get(BZ.get()).toUpperCase();   //
        } catch (IndexOutOfBoundsException e) {
            listener.errorEncountered("FEHLER: Unerwartetes Programmende! " +
                    "You made a CPU cry! Shame on you!", BZ.get());
            ausführendState.set(false);
            
            return;
        }
        auszuführendeAnweisung  = auszuführendeAnweisung.trim();

        if(auszuführendeAnweisung.equals("") || auszuführendeAnweisung.substring(0, 2).equals("--")) {
            BZ.set(BZ.get() + 1);
            listener.updateHighlighting(BZ.get());
            return;
        }

        if(auszuführendeAnweisung.startsWith("END")) {
            System.out.println("------END ENCOUNTERED!!!");
            BZ.set(BZ.get() + 1);
            
            ausführendState.set(false);
            listener.endEncountered();
            return;
        }

        if(auszuführendeAnweisung.startsWith("NOP")) {
            BZ.set(BZ.get()+1);
            listener.updateHighlighting(BZ.get());
            return;
        }


        Scanner sc = new Scanner(auszuführendeAnweisung);
        String operation = sc.next();
        int arg;
        if(sc.hasNextInt())
            arg = sc.nextInt();
        else {
            listener.errorEncountered("FEHLER: In Zeile " + (BZ.get()+1) + " fehlt der Integer " +
                    "nach der Anweisung. ", BZ.get());

            ausführendState.set(false);
            
            return;
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
                if(R[arg-1].get() == 0) {
                    divBy0();
                    return;
                }
                A.set(A.get() / R[arg-1].get());
                BZ.set(BZ.get()+1);
                break;
            case "MOD":
                if(R[arg-1].get() == 0) {
                    divBy0();
                    return;
                }
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
                 
                 return;
        }
        listener.updateHighlighting(BZ.get());
    }




    public SimpleIntegerProperty[] getR() {
        return R;
    }

    public int getBZ() {
        return BZ.get();
    }



    private void divBy0() {
        
        listener.errorEncountered("LAUFZEITFEHLER: Division durch 0", BZ.get());
        ausführendState.set(false);

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

    public SimpleBooleanProperty isTimedProperty() {
        return isTimed;
    }



}
