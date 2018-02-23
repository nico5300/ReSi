package me.ocin5300.ReSi.logik;

public interface RegistermaschinenListener {
    void updateStatus(String msg);
    void errorEncountered(String msg, int line);
    void endEncountered();
    void updateHighlighting(int line);
}
