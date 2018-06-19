# ReSi
Wie SeRmS, nur besser.

### Benötigte Dependencies:
[RichtextFX](https://mvnrepository.com/artifact/org.fxmisc.richtext/richtextfx/0.9.0)
Version 0.9.0

[afterburner.fx](https://mvnrepository.com/artifact/com.airhacks/afterburner.fx/1.7.0)
Version 1.7.0

### In IntelliJ hinzufügen:
1. File > Project Structure
2. Auf der linken Seite auf Libraries klicken
3. In der zweiten Spalte oben auf das grüne Pluszeichen klicken > From Maven...
4. Im erscheinenden Fenster ins Textfeld `afterburner.fx` eintippen und auf die Lupe 
    klicken. Darauf warten, dass im Dropdown-Menü afterburner.fx in Version 1.7.0 erscheint
    (Der Haken bei "Transitive Dependencies" muss gesetzt sein!). Auswählen und Okay klicken.
5. Das gleiche mit RichtextFX (Version 0.9.0) machen

### Sonstige Einstellungen, die wichtig wären:
- Darauf achten, dass in der Project Structure unter dem Punkt "Project" das Language Level
    und das Project SDK auf 1.8 stehen
- In der Project Structure muss noch ein Compiler Output Path spezifiziert werden: Wieder
    unter Project Structure > Project einfach einen Ordner namens "out" im Wurzelverzeichnis
    des Projekts erstellen
- Damit man das Projekt erfolgreich kompilieren kann, muss man IntelliJ noch beibringen, in
    welchem Ordner der Quelltext jetzt eigentlich steckt: einfach Rechtsklick auf den "src" 
    Ordner > Mark Directory As > Mark As Sources Root
    
### Wie man eine Jar exportiert:
1. File > Project Structure > Artifacts
2. Grünes Pluszeichen > Jar > From Modules with Dependencies...
3. Im auftauchenden Fenster muss die Main-Klasse angegeben werden: `me.ocin5300.ReSi.Main`
4. Okay und Okay...
5. Build > Build Artifacts... > Rebuild
6. Unter `./out/artifacts` findet man die Jar-Datei die jetzt Standalone ist und alle 
    Dependencies enthält 
    
#### Final Thoughts...

Jetzt sollte das kompilieren endlich funktionieren... Wenn ich irgendwann bei Maven
    durchsteige, dann lade ich es als Maven-Projekt hoch, welches sich dann selbstständig
    um die Dependencies kümmert