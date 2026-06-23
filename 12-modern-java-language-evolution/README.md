# Modulo 12 — Modern Java Language Evolution

> **Era:** 2021+ · **JDK:** 17+ (LTS) · **Paradigma:** Expressivo, tipado, funcional

---

## 1. Contexto historico

Em 2021, a Oracle lancou o **Java 17**, o LTS mais importante desde o Java 11.
Ele trazia tres features que transformariam a expressividade da linguagem:

- **Records** (`record Point(int x, int y) {}`) — DTOs imutaveis em uma linha
- **Sealed classes** (`sealed interface Shape permits Circle, Square {}`) —
  hierarquias de tipos fechadas
- **Pattern Matching for instanceof** — `if (obj instanceof String s)`
- **Switch expressions** — `return switch (shape) { case Circle c -> ... };`

Em 2023, o Java 21 (LTS) adicionou:
- **Virtual Threads** (Project Loom) — threads leves para concorrencia massiva
- **Record Patterns** — `if (obj instanceof Point(int x, int y))`
- **Pattern Matching for switch** — `case Circle(var raio) -> ...`
- **Sequenced Collections** — `list.getFirst()`, `list.getLast()`

A mensagem era clara: Java estava competindo em expressividade com Kotlin,
Scala, C# e TypeScript, sem sacrificar sua maior vantagem — compatibilidade
retrospectiva e tipagem forte.

## 2. Problema historico

Ate o Java 16 (2021), um DTO simples exigia:
```java
// Pre Java 16: ~50 linhas para uma classe de dados
public class Cliente {
    private String nome;
    private String email;

    public Cliente(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }

    @Override public boolean equals(Object o) { ... }
    @Override public int hashCode() { ... }
    @Override public String toString() { ... }
}
```

Com records, a mesma coisa:
```java
public record Cliente(String nome, String email) {}
```

Modelagem de dominio algebrico era impossivel sem bibliotecas externas
ou patterns complexos. Pattern matching exigia `instanceof` + cast:
```java
// Pre Java 16
if (animal instanceof Cachorro) {
    Cachorro c = (Cachorro) animal;
    c.latir();
}

// Java 16+
if (animal instanceof Cachorro c) {
    c.latir();
}
```

## 3. Aplicacao: Motor de regras para analise de solicitacoes

Um motor de regras que analisa diferentes tipos de solicitacoes financeiras:
- **Emprestimo**: analisa comprometimento de renda
- **Credito**: verifica restricao cadastral e limite disponivel
- **Consorcio**: verifica parcelas minimas pagas

Usa:
- **Sealed interface** `Solicitacao` com subtipos `Emprestimo`, `Credito`, `Consorcio`
- **Records** para DTOs imutaveis
- **Pattern matching for switch** para logica de negocio
- **Switch expressions** para resultados tipados
- **Records** para resultados (`Aprovada`, `Negada`, `RevisaoManual`)
- **Enhanced switch** sem fall-through
- **Text blocks** para saida formatada

## 4. Recursos do Java moderno relevantes

| Recurso | JDK | Relevancia neste modulo |
|---------|-----|------------------------|
| `record` | 16 (preview), 17 (stable) | DTOs imutaveis: `Solicitacao.Emprestimo`, `RegraAnalise.Aprovada` |
| `sealed interface` | 15 (preview), 17 (stable) | Hierarquia fechada de tipos: `Solicitacao` |
| `sealed interface` + `record` | 17 | Combinação poderosa: subtipos como records |
| Pattern matching `switch` | 17 (preview), 21 (stable) | `case Emprestimo(var _, var valor, ...) ->` |
| `switch expression` | 14 (preview), 17 (stable) | `return switch(s) { case ... -> ... }` |
| `instanceof` pattern matching | 16 (preview), 17 (stable) | `if (resultado instanceof Aprovada a)` |
| Text blocks `""" ` | 13 (preview), 17 (stable) | Strings multi-linha para output formatado |
| `var` | 10 | Inferencia de tipo local: `var solicitacoes = List.of(...)` |
| `List.of()` / `Stream.toList()` | 9 / 16 | Colecoes imutaveis, collectors simplificados |

## 5. Arquitetura e design

```
Main.main()
  └── List<Solicitacao>  ← records imutaveis
        ├── Emprestimo(cliente, valor, parcelas, rendaMensal)
        ├── Credito(cliente, valorSolicitado, limiteDisponivel, possuiRestricao)
        └── Consorcio(cliente, valorTotal, totalParcelas, parcelasPagas)
        │
        ├── solicitacoes.forEach(s -> RegraAnalise.analisar(s))
        │     │
        │     └── switch expression com pattern matching
        │           ├── case Emprestimo(var _, double valor, int p, double r) -> calcu
la
        │           ├── case Credito(var _, double valor, double limite, boolean restr
) -> check
        │           └── case Consorcio(var _, ..., int pagas) -> check
        │
        └── Resultado (sealed interface)
              ├── Aprovada(String motivo)
              ├── Negada(String motivo)
              └── RevisaoManual(String motivo)
```

**Fluxo de analise:**
```
Solicitacao recebida → switch pattern matching →
  ├── Regra de negocio especifica (sealed garante exaustividade)
  └── Resultado tipado (Aprovada | Negada | RevisaoManual)
        └── pattern matching para exibir resultado
```

## 6. Limitacoes e dificuldades

### Records sao finais

Records nao podem ser estendidos. Para modelagem de dominio que precisa
de hierarquia, combinamos records com sealed interfaces — os records
sao as implementacoes concretas.

### Sealed classes exigem declaracao explicita

Todos os subtipos permitidos devem ser declarados na clausula `permits`.
Em dominios grandes, isso pode ser verboso. O compilador, porem, garante
que nenhum subtipo esta faltando.

### Pattern matching ainda evoluindo

No JDK 17:
- Pattern matching for instanceof: OK
- Pattern matching for switch: preview
- Record patterns: preview
- Pattern matching para arrays: nao disponivel

No JDK 21:
- Pattern matching for switch: stable
- Record patterns: stable
- Virtual threads: stable

### Compatibilidade com versoes anteriores

Codigo que usa records, sealed classes e pattern matching requer
JDK 17+ para compilar e executar. Isso pode ser problematico em
ambientes corporativos que ainda usam JDK 8 ou 11.

## 7. Peca de museu

Java 17+ mostra que uma linguagem com 25+ anos pode aprender novos truques.
Records reduzem boilerplate a quase zero. Sealed classes trazem modelagem
de dominio algebrica. Pattern matching elimina decadas de codigo defensivo.

O mais impressionante: tudo isso foi adicionado **sem quebrar codigo existente**.
Um `HashMap` de 1997 ainda compila e funciona no JDK 21. Essa compatibilidade
retrospectiva e o superpoder do Java — e tambem sua maior limitacao para
inovar rapidamente.

Java nao e mais a linguagem verbosa dos anos 2000. E uma linguagem moderna
que compete em expressividade com Kotlin e C#, mantendo o ecossistema,
a performance e a confiabilidade que construiu por quase tres decadas.

## 8. Como executar

**Pre-requisito:** JDK 17+

```bash
cd 12-modern-java-language-evolution
chmod +x build.sh
./build.sh
```

Ou com Maven:
```bash
mvn clean compile exec:java
```

Ou manualmente:
```bash
javac --release 17 -d out -sourcepath src src/regras/*.java
java -cp out regras.Main
```

## 9. O que essa era resolveu

Comparado ao Java 8-11 (modulos 09-11):
- DTOs: 1 linha com `record` vs ~50 linhas com classes tradicionais
- Hierarquias fechadas: `sealed` vs documentacao informal
- Pattern matching: `instanceof X x` vs `instanceof + cast`
- Switch: expressao com retorno vs statement com fall-through
- Tipos algebricos: combinacao sealed + record vs classes abstratas + heranca
- Strings multi-linha: text blocks vs concatenacao manual

## 10. O que essa era ainda nao resolvia

- Value types (Project Valhalla) — ainda em preview no JDK 25
- Pattern matching total (arrays, mais deconstruction)
- Serializacao automatica para records (ainda exige bibliotecas externas)
- Universal generics over primitives (Project Valhalla)
- Struct-like types sem overhead de objeto
- Consistencia total entre var e tipos explicitos

## 11. O que veio depois

| Feature | JDK | Impacto |
|---------|-----|---------|
| Virtual Threads (Loom) | 21 | Concorrencia massiva com threads leves |
| Record Patterns | 21 | Deconstruction de records em pattern matching |
| Sequenced Collections | 21 | `getFirst()`, `getLast()`, `reversed()` em todas colecoes |
| Unnamed Variables `_` | 22 | Variaveis ignoradas em pattern matching |
| Stream Gatherers | 22 | Operacoes de stream customizadas |
| Value Types (Valhalla) | Preview 25+ | Tipos primitivos genericos com semantica de valor |
| String Templates | Preview 23+ | Interpolacao segura de strings |
