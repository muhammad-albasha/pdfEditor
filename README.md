
# PDF Editor GUI mit Apache PDFBox

Dies ist eine Java-Anwendung mit grafischer Benutzeroberfläche zum Anzeigen und Bearbeiten von PDF-Dokumenten. Die Anwendung ermöglicht es Ihnen, PDF-Dateien zu öffnen, Seiten anzuzeigen, zwischen Seiten zu navigieren, zu zoomen und den Text auf jeder Seite zu bearbeiten.

## Funktionen

- **PDF-Dateien öffnen**: Laden Sie eine PDF-Datei in die Anwendung.
- **PDF-Dateien speichern**: Speichern Sie die Änderungen an der PDF-Datei.
- **Seitenansicht**: Anzeigen der PDF-Seiten mit Navigationsmöglichkeiten (Vorherige/Nächste Seite).
- **Zoom-Funktion**: Vergrößern oder Verkleinern der Seitenansicht.
- **Textbearbeitung**: Doppelklicken Sie auf die PDF-Seite, um den Text der aktuellen Seite zu bearbeiten.
- **Seitenwechsel**: Gehen Sie zu einer bestimmten Seite, indem Sie die Seitennummer eingeben.

## Voraussetzungen

- **Java Development Kit (JDK)**: Version 8 oder höher.
- **Apache PDFBox**: Version 2.0.29 oder höher.
- **Schriftart "Arial"**: Die TrueType-Schriftartdatei `arial.ttf` muss im Ressourcenordner vorhanden sein.

## Installation und Einrichtung

### 1. Repository klonen

Klonen Sie dieses Repository auf Ihren lokalen Rechner:

```bash
git clone https://github.com/IhrBenutzername/IhrRepository.git
```

### 2. Abhängigkeiten hinzufügen

Stellen Sie sicher, dass Sie Apache PDFBox in Ihrem Projekt als Abhängigkeit hinzufügen.

**Wenn Sie Maven verwenden**, fügen Sie die folgende Abhängigkeit zu Ihrer `pom.xml` hinzu:

```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>2.0.29</version>
</dependency>
```

**Wenn Sie Gradle verwenden**, fügen Sie Folgendes zu Ihrer `build.gradle` hinzu:

```gradle
implementation 'org.apache.pdfbox:pdfbox:2.0.29'
```

### 3. Ressourcenordner einrichten

Erstellen Sie in Ihrem Projekt den Ressourcenordner `src/main/resources/fonts/` und legen Sie die Schriftartdatei `arial.ttf` dort ab.

Die Projektstruktur sollte wie folgt aussehen:

```
IhrProjekt/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/
│   │   │       └── apache/
│   │   │           └── pdfbox/
│   │   │               └── PDFEditorGUI.java
│   │   └── resources/
│   │       └── fonts/
│   │           └── arial.ttf
```

### 4. Schriftart bereitstellen

Stellen Sie sicher, dass die Schriftartdatei `arial.ttf` eine gültige TrueType-Schriftart ist. Sie können diese von Ihrem Systemschriftartenordner kopieren oder eine alternative freie Schriftart wie `DejaVuSans.ttf` verwenden.

**Hinweis**: Achten Sie auf die Lizenzbedingungen der Schriftart.

## Ausführen der Anwendung

1. **Kompilieren Sie den Code**:

   Wechseln Sie in das Projektverzeichnis und führen Sie den folgenden Befehl aus, um die Klassen zu kompilieren:

   ```bash
   javac -d bin -sourcepath src/main/java src/main/java/org/apache/pdfbox/PDFEditorGUI.java
   ```

2. **Ausführen der Anwendung**:

   Führen Sie die Anwendung mit dem folgenden Befehl aus:

   ```bash
   java -cp bin;path/to/pdfbox-app-2.0.29.jar org.apache.pdfbox.PDFEditorGUI
   ```

   **Hinweis**: Ersetzen Sie `path/to/pdfbox-app-2.0.29.jar` durch den tatsächlichen Pfad zu Ihrer PDFBox JAR-Datei.

## Verwendung der Anwendung

- **PDF-Datei öffnen**: Klicken Sie auf die Schaltfläche "Öffnen" und wählen Sie eine PDF-Datei aus.
- **Seiten navigieren**: Verwenden Sie die Schaltflächen "Vorherige Seite" und "Nächste Seite" oder geben Sie die Seitennummer in das Textfeld ein.
- **Zoomen**: Verwenden Sie die Schaltflächen "Vergrößern" und "Verkleinern".
- **Text bearbeiten**: Doppelklicken Sie auf die angezeigte PDF-Seite, um den Texteditor zu öffnen. Nehmen Sie Ihre Änderungen vor und klicken Sie auf "OK", um sie zu übernehmen.
- **PDF speichern**: Klicken Sie auf die Schaltfläche "Speichern", um die Änderungen zu speichern.

## Bekannte Einschränkungen

- **Formatierung**: Beim Bearbeiten des Textes geht die ursprüngliche Formatierung (Schriftart, Schriftgröße, Farbe, Position) verloren. Der neue Text wird mit der angegebenen Schriftart und Schriftgröße eingefügt.
- **Komplexe PDFs**: Die Anwendung eignet sich für einfache PDFs mit Textinhalt. Bei PDFs mit komplexen Layouts, Bildern oder Formularen kann es zu unerwarteten Ergebnissen kommen.
- **Zeichenunterstützung**: Stellen Sie sicher, dass die verwendete Schriftart alle benötigten Zeichen unterstützt, insbesondere bei Sonderzeichen oder nicht-lateinischen Schriftzeichen.

## Anpassungen

### Verwenden einer anderen Schriftart

Wenn Sie eine andere Schriftart verwenden möchten, ersetzen Sie die Schriftartdatei im Ressourcenordner und passen Sie den Pfad in der Methode `replaceTextInPDF` an:

```java
InputStream fontStream = getClass().getResourceAsStream("/fonts/IhreSchriftart.ttf");
```

### Anpassung der Schriftgröße

Sie können die Schriftgröße ändern, indem Sie den Wert der Variable `fontSize` in der Methode `replaceTextInPDF` anpassen.

## Fehlerbehebung

- **Schriftart nicht gefunden**: Stellen Sie sicher, dass die Schriftartdatei im richtigen Pfad liegt und dass der Pfad in `getResourceAsStream` korrekt ist.
- **Fehlermeldungen beim Laden oder Speichern von PDFs**: Überprüfen Sie, ob die PDF-Datei nicht beschädigt ist und dass Sie über die erforderlichen Berechtigungen verfügen.
- **Ungültige Seitenzahl**: Stellen Sie sicher, dass die eingegebene Seitennummer innerhalb des Bereichs der PDF-Seiten liegt.

## Beiträge

Beiträge zu diesem Projekt sind willkommen. Bitte erstellen Sie einen Fork des Repositories und senden Sie Pull Requests mit Ihren Änderungen.

## Lizenz

Dieses Projekt steht unter der **MIT-Lizenz**. Weitere Informationen finden Sie in der Datei [LICENSE](LICENSE).

## Danksagungen

- **Apache PDFBox**: Für die Bereitstellung einer leistungsstarken Bibliothek zur PDF-Verarbeitung.
- **Open-Source-Community**: Für die Unterstützung und Bereitstellung von Ressourcen.
