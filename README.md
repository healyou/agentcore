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

## Доработка
После реализации основной части многоагентной системы(Всё, что заявлено в пз работает) 
можно доработать следующее(! - наиболее важная доработка):
* ! без команды clean compile assembly:single не обновляется jar с зависимостями в лок. реп. maven
* ! При создании агента в web приложении login и masId в dsl должны совпадать
(как-то надо изменять логику создания агента)
* JafaFx gui вынести в отдельный модуль
* Тесты web модуля хоть какие-то и RuntimeAgentWorkControl
* В web модуле что-то не-так с подключением css, js(в проекте есть todo на ошибку)