# NetSiL

**NetSiL — Hálózati Forgalom Monitor**: Windows hálózati kapcsolatok és
forgalom valós idejű megfigyelése, LCARS-stílusú (Star Trek-ihletésű)
felülettel.

## Futtatás

```
java -jar dist/netsil.jar
```

(Java futtatókörnyezet szükséges a gépen. A program a Windows
`netstat -ano` parancsát hívja a háttérben, ezért csak Windows alatt
működik.)

### Fordítás forrásból

```
javac -d out -encoding UTF-8 $(find src -name "*.java")
jar --create --file dist/netsil.jar --main-class netsil.Main -C out .
```

(Lásd a [`package-exe.ps1`](package-exe.ps1) szkriptet natív Windows
`.exe` csomag készítéséhez beágyazott Java futtatókörnyezettel, vagy a
[`run.ps1`](run.ps1)-t gyors futtatáshoz.)

## Főbb funkciók

- Aktív hálózati kapcsolatok listázása (Windows `netstat -ano` kimenet
  feldolgozása, `NetstatParser`)
- Folyamatnév-feloldás a kapcsolatokhoz (`ProcessNameResolver`)
- Hálózati interfész statisztikák (`InterfaceStats`, `InterfacePanel`)
- Szűrés port és egyéb feltételek szerint (`PortFilter`,
  `FilterCriteria`, `FilterBar`)
- Forgalmi táblázat (`TrafficTable`) és donut-diagram (`DonutChart`)
- LCARS-stílusú felület saját komponensekkel (lekerekített panelek,
  pill gombok/címkék, sarokelemek)

## Mappaszerkezet

- [`src/netsil/`](src/netsil/) — forráskód
  - `net/` — netstat-feldolgozás, kapcsolat-/interfész-modell, figyelő
    szolgáltatás
  - `filter/` — szűrési feltételek
  - `ui/` — LCARS-stílusú Swing felület
- [`dist/netsil.jar`](dist/netsil.jar) — futtatható jar

## Licenc

A projekt a [PolyForm Noncommercial 1.0.0](LICENSE) licenc alatt áll: szabadon
nézhető, módosítható és terjeszthető, de csak nem kereskedelmi célra.
