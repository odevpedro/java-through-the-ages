# Modulo 06 — Java ME Field Service Mobile

> Era: 2003–2008 · Plataforma: MIDP 2.0 / CLDC 1.1 · Dispositivo: celular 128 KB RAM

## 1. Contexto historico
1 billion Java ME phones by 2005. Nokia, Motorola, Sony Ericsson. CLDC 1.1 (no generics, no File I/O, no reflection, no ArrayList, no HashMap). LCDUI for UI.

## 2. Problema historico
Field technicians needed mobile data collection: client codes, visit status, observations. No smartphones yet. Java ME was the standard platform for mobile enterprise apps.

## 3. Aplicacao: Aplicativo de vistoria tecnica offline
Technician enters client code, selects visit status, saves observation. Data persisted in RecordStore (RMS).

## 4. APIs do CLDC/MIDP

| API | Uso |
|-----|-----|
| `javax.microedition.midlet.MIDlet` | Ciclo de vida do aplicativo |
| `javax.microedition.lcdui.*` | Interface: Form, List, TextField, ChoiceGroup, Command |
| `javax.microedition.rms.RecordStore` | Persistencia local (RMS) |
| `DataInputStream` / `DataOutputStream` | Serializacao binaria |
| `Vector` / `Hashtable` | Colecoes disponiveis (sem generics) |
| `Calendar` | Manipulacao de datas |

## 5. Arquitetura

```
VistoriaMidlet
    |
    +--> Display.setCurrent(TelaListaVistorias)
    |        |
    |        +--> List (inspections saved)
    |        +--> Command: Nova vistoria -> TelaVistoria
    |        +--> Command: Detalhes -> Alert
    |
    +--> Display.setCurrent(TelaVistoria)
             |
             +--> Form (codigo, status, observacao)
             +--> Command: Salvar -> RepositorioRMS.salvar()
             +--> Command: Voltar -> TelaListaVistorias

RepositorioRMS -> RecordStore (RMS) -> disco
```

## 6. Limitacoes
- 128 KB heap max
- preverify required
- Text input with T9 keyboard
- Screen sizes 96x65 to 176x220
- No SQL, only RecordStore byte arrays
- Fragmentacao extrema entre fabricantes
- Sem reflection, sem generics, sem File I/O

## 7. Peca de museu
Java ME teaches constraint-driven development better than any other platform. Writing code for 128 KB heap where every object allocation is a conscious decision is the antidote to modern "just import a library" mindset. The iPhone killed Java ME in 3 years.

## 8. Build e execucao
Requires WTK 2.5, MicroEmu, or just compile in modern JDK for code review.

## 9. Evolucao posterior
Android SDK, SQLite, smartphone hardware
