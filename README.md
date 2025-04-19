# Jack Analyzer - Nand2Tetris Project 10

This project is part of the [Nand2Tetris](https://www.nand2tetris.org/) course.  
It implements a two-stage syntax analyzer for the Jack programming language using Java.

---

## 🛠 Features

- **JackTokenizer.java** – breaks `.jack` code into tokens  
- **CompilationEngine.java** – parses the tokenized input and generates structured XML
- Follows the grammar and structure defined by the Jack language specification
- Output includes both token-level XML and full parse tree XML
- Includes sample programs for testing and comparison against reference XML

---

## 📂 Key Files & Structure

- `src/main/java/jackanalyzer/JackTokenizer.java` – Tokenizes input into keywords, symbols, identifiers, etc.  
- `src/main/java/jackanalyzer/CompilationEngine.java` – Constructs full syntax tree in XML format  
- `src/main/java/jackanalyzer/JackAnalyzer.java` – File I/O manager for `.jack` and output XML  
- `src/test/java/jackanalyzer/Test.jack` – Sample input file for analyzer testing  
- `ArrayTest/`, `Square/`, `ExpressionLessSquare/` – Sample Jack programs  
- `Squarecompare/`, `ExpressionLessSquarecompare/` – Expected XML outputs for validation  
- `output/` – Generated XML output from this implementation  
- `pom.xml` – Maven project config  
- `.gitignore` – Project cleanup

---

## ▶️ How to Run

```bash
mvn compile
mvn exec:java -Dexec.mainClass="jackanalyzer.JackAnalyzer" -Dexec.args="Square/Main.jack"
```
## 📌 Example [Input (Jack)]
```
class Main {
   function void main() {
      var int x;
      let x = 2;
      return;
   }
}
```
## Output (Simplified XML)
```
<class>
  <keyword> class </keyword>
  <identifier> Main </identifier>
  ...
</class>
```

## 👨‍💻 Author

Ohad Swissa
Honors Student – Computer Science & Entrepreneurship
Ex-IDF Special Forces Major | Problem Solver
[LinkedIn](https://www.linkedin.com/in/ohad-swissa-54728a2a6)

