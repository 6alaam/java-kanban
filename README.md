день добрый.
изменил тест InMemoryHistoryManagerTest , правда не ддодумался как нормально в тесте прикручивать туда айди, поэтому временно создал кострукторы.
связный список LinkedList мы будем проходить только в следующем спринте, времени очень не хватает по этому прошу принять пока так.
return new InMemoryTaskManager (getDefaultHistoryManager()); пока не могу додуматься , подглядел различные варианты, типа  public static TaskManager getHttpTaskManager(String...) {return new HttpTaskManager(...);}
но как переделать код в остальных классах не понимаю







Обобщаем класс «Менеджер»
При проектировании классов и их взаимодействия бывает полезно разделить описание функций класса на интерфейс и реализацию. Из темы об абстракции и полиморфизме вы узнали, что такое интерфейсы и как их использовать для выделения значимой функциональности классов (абстрагирования). При таком подходе набор методов, который должен быть у объекта, лучше вынести в интерфейс, а реализацию этих методов — в класс, который его реализует. Теперь нужно применить этот принцип к менеджеру задач.
Класс TaskManager станет интерфейсом. В нём нужно собрать список методов, которые должны быть у любого объекта-менеджера. Вспомогательные методы, если вы их создавали, переносить в интерфейс не нужно.
Созданный ранее класс менеджера нужно переименовать в InMemoryTaskManager. Именно то, что менеджер хранит всю информацию в оперативной памяти, и есть его главное свойство, позволяющее эффективно управлять задачами. Внутри класса должна остаться реализация методов. При этом важно не забыть имплементировать TaskManager, ведь в Java класс должен явно заявить, что он подходит под требования интерфейса.

Добавьте в программу новую функциональность — нужно, чтобы трекер отображал последние просмотренные пользователем задачи. Для этого добавьте метод getHistory в TaskManager и реализуйте его — он должен возвращать последние 10 просмотренных задач. Просмотром будем считать вызов тех методов, которые получают задачу по идентификатору, — getTask(int id), getSubtask(int id) и getEpic(int id). От повторных просмотров избавляться не нужно. 

У метода getHistory не будет параметров. Это значит, что он формирует свой ответ, анализируя исключительно внутреннее состояние полей объекта менеджера. Подумайте, каким образом и какие данные вы запишете в поля менеджера для возможности извлекать из них историю посещений. Так как в истории отображается, к каким задачам было обращение в методах getTask, getSubtask и getEpic, эти данные в полях менеджера будут обновляться при вызове этих трёх методов.
Со временем в приложении трекера появится несколько реализаций интерфейса TaskManager. Чтобы не зависеть от реализации, создайте утилитарный класс Managers. На нём будет лежать вся ответственность за создание менеджера задач. То есть Managers должен сам подбирать нужную реализацию TaskManager и возвращать объект правильного типа.
У Managers будет метод getDefault. При этом вызывающему неизвестен конкретный класс — только то, что объект, который возвращает getDefault, реализует интерфейс TaskManager.
Сделайте историю задач интерфейсом
В этом спринте возможности трекера ограничены — в истории просмотров допускается дублирование, и она может содержать только десять задач. В следующем спринте вам нужно будет убрать дубли и расширить её размер. Чтобы подготовиться к этому, проведите рефакторинг кода. 
Создайте отдельный интерфейс для управления историей просмотров — HistoryManager. У него будет два метода: add(Task task) должен помечать задачи как просмотренные, а getHistory — возвращать их список. 
Объявите класс InMemoryHistoryManager и перенесите в него часть кода для работы с историей из класса InMemoryTaskManager. Новый класс InMemoryHistoryManager должен реализовывать интерфейс HistoryManager. 
Добавьте в служебный класс Managers статический метод HistoryManager getDefaultHistory. Он должен возвращать объект InMemoryHistoryManager — историю просмотров. 
Проверьте, что теперь InMemoryTaskManager обращается к менеджеру истории через интерфейс HistoryManager и использует реализацию, которую возвращает метод getDefaultHistory.


Не нужно покрывать тестами весь код. Даже для такого небольшого проекта тестирование может занять значительное время. Но всё же обратите внимание на некоторые нюансы, которые необходимо проверить:
проверьте, что экземпляры класса Task равны друг другу, если равен их id;
проверьте, что наследники класса Task равны друг другу, если равен их id;
проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
проверьте, что объект Subtask нельзя сделать своим же эпиком;
убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.











Простейший кирпичик трекера — задача (англ. task). У неё есть следующие свойства:
Название, кратко описывающее суть задачи (например, «Переезд»).
Описание, в котором раскрываются детали.
Уникальный идентификационный номер задачи, по которому её можно будет найти.
Статус, отображающий её прогресс. Вы будете выделять следующие этапы жизни задачи, используя enum:
1. NEW — задача только создана, но к её выполнению ещё не приступили.
2. IN_PROGRESS — над задачей ведётся работа.
3. DONE — задача выполнена.
Иногда для выполнения какой-нибудь масштабной задачи её лучше разбить на подзадачи (англ. subtask). Большая задача, которая делится на подзадачи, называется эпиком (англ. epic). 
Подытожим. В системе задачи могут быть трёх типов: обычные задачи, эпики и подзадачи. Для них должны выполняться следующие условия:
Для каждой подзадачи известно, в рамках какого эпика она выполняется.
Каждый эпик знает, какие подзадачи в него входят.
Завершение всех подзадач эпика считается завершением эпика.
