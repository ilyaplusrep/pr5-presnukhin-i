# Практическая работа №5

Строки, массивы, циклы и методы обработки данных. Анализ табеля рабочего времени за неделю.

Вариант 16: переработкой считается день с v > 10; добавлен метод `addHeavyDaysCount` (дни с v > 12).

## Требования

Java SE 21+ (без Maven/Gradle).

## Сборка и запуск

Linux / macOS / Git Bash:

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out ru.edu.pr02.SelfCheck
```

Windows:

```
run_selfcheck.bat
```

## Интерактивный режим

После проверок программа читает строки табеля с консоли (выход: `exit` или пустая строка):

```bash
java -cp out ru.edu.pr02.SelfCheck -i
```

или `run_interactive.bat` на Windows.

## Структура

- `src/ru/edu/pr02/TimesheetAnalyzer.java` – бизнес-логика (разбор и расчёты)
- `src/ru/edu/pr02/SelfCheck.java` – точка входа: unit-проверки, data-driven, интерактивный режим
- `data/sample.csv` – контрольные значения (`вход|ожидание|комментарий`)
- `diagrams/` – Activity-диаграмма и диаграмма классов (`.dot` и `.png`)
- `DECISIONS.md` – вариант, решения, ответы на вопросы
