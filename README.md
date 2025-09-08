# GradeFX

Software zur Notenverwaltung für Lehrkräfte.

Achtung: Diese Software befindet sich in einer frühen Entwicklungsphase! Daher können Ausfälle, Abstürze und unverhergesehenes Verhalten auftreten und möglicherweise zu Datenverlust führen. Bitte regelmäßig Sicherheitskopien anlegen bzw. Daten exportieren.

## Features
Folgende Features sind bereits enthalten:
* Verwaltung mehrerer Klassen in einer Datei (Eine Datei pro Schuljahr genügt)
* Verwaltung mehrerer Leistungsnachweise in den Klassen
* Leistungsnachweis-Gruppen (kleine, große) mit entsprechender Gewichtung vorhanden und voll anpassbar
* Korrekte Berechnung von Endnoten, Schniten usw.
* Anpassbare Punkteschlüssel (Schieberegler und Direkteingabe), halbe Punkte optional
* Leistungsnachweise ohne Punkte (z.B. Unterrichtsbeiträge) sind möglich
* Notentendenzen sind optional möglich (Grenze flexibel)
* Individuelles Datum und Text-Anmerkung für Noten
* SchülerInnen-Import per csv-Datei
* Sortieren von Tabellen nach Name, Note, usw.
* Ausdruck/PDF-Export möglich (wird noch verbessert)
* Verschlüsselte Dateien (können beliebig in Cloud-Diensten oder aus USB-Sticks gelagert werden, sofern gute Passwörter verwendet werden)

Geplante Features
* Automatisches Ausfüllen von Umschlägen für Archivierung
* Filter, insbesondere in der Übersicht (z.B. Stand zu bestimmtem Datum, Noten einzelnder SchülerInnen)
* Weitere Statistiken (Diagramme für Notenverteilung, Abschneiden bei einzelnen Aufgaben)
* Update-Benachrichtigung

Derzeit nicht umsetzbare Features
* ASV-Export

## Download
Das Programm wird als einzelne ausführbare Datei (ohne Installation) angeboten. Zusätzlich wird lediglich beim ersten Start eine Konfigurations-Datei im Home-Verzeichnis des Benutzers angelegt.

Unter [Release](https://github.com/fabiankaschta/GradeFX/releases/latest) finden sich jeweils die aktuellen Versionen.
* Windows: Das Programm wird als exe-Datei angeboten. Diese wird von Virenschutzprogrammen wahrscheinlich erstmal als gefährlich eingestuft. Zudem wird eine aktuelle Version von [Microsoft Visual C++ Redistributable](https://learn.microsoft.com/de-de/cpp/windows/latest-supported-vc-redist?view=msvc-170#latest-microsoft-visual-c-redistributable-version) benötigt.
* Die ausführbaren Java-Dateien (*.jar), die für alle Betriebssysteme verfügbar sind, benötigen mindestens [Java 24](https://jdk.java.net/24/). Dies ist jedoch nicht für Endnutzer gedacht, von einer Verwendung dieser Versionen wird daher abgeraten.
