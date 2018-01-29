# agentcore
Проект распределённой многоагентной системы, находится на стадии ранней разработки.

## Authors
* **Nikita Gorodilov** - *Initial work* - [healyou](https://github.com/healyou)

## Настройка проекта для запуска
Необходимо изменить пути в следующих файлах:
* jetty-env.xml (webapp module)
* logback.xml (agentcore module)
* context.properties (agentcore module)
* testJdbc.properties (agentcore module)

После install agentcore необходимо также выполнить следующее(пока непонтно почему):
* clean compile assembly:single (обновит jar в локальном репозитории maven-а)