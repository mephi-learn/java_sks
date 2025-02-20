# "**Итоговый проект «Сервис коротких ссылок»**"


При запуске выводится меню:

![](img/image1.png)

В заголовке отображается аутентифицированный пользователь, а меню
состоит из следующих элементов:

1.  Аутентификация по UUID -- если пользователь знает свой UUID, то
    выбрав данный пункт он сможет его ввести и, в случае удачи, будет
    аутентифицирован. Также его UUID будет отображаться в заголовке
    меню:

![](img/image2.png)

В случае неудачи будет выведена ошибка:

![](img/image3.png)

2.  Создание короткой ссылки -- при выборе будет предложено ввести адрес
    ссылки, максимальное количество переходов и время жизни. Некоторые
    параметры имеют значение по-умолчанию, в этом случае можно просто
    нажать Enter. Если ссылка не начинается с http или https, то она
    автоматически дополнится префиксом https. При этом, если
    пользователь не был аутентифицирован, автоматически произойдёт
    создание и аутентификация нового пользователя, а также будет
    выведено сообщение об этом. Также будет выведена информация о
    созданной короткой ссылке:

![](img/image4.png)

Если пользователь уже был аутентифицирован, то будет выведена только
информация о короткой ссылке:

![](img/image5.png)

Если пользователь ранее уже сохранял эту ссылку об этом будет выведено
сообщение (при этом учитываются ссылки только конкретного пользователя):

![](img/image6.png)

3.  Переход по короткой ссылке -- тут можно ввести короткую ссылку,
    произойдёт переход по сохранённому адресу. При этом будет уменьшено
    количество переходов и учтено время жизни ссылки. Как только будет
    достигнуто какое-либо ограничение, об этом будет выведено сообщение:

![](img/image7.png)

Также можно указать одну или несколько ссылок в качестве аргументов при
запуске программы. В этом случае программа запустит те ссылки, которые
возможно запустить, выведет информацию о тех ссылках, которые запустить
невозможно и выйдет, не выводя меню.

4.  Редактирование ссылки -- пункт доступен только, если пользователь
    аутентифицирован:

![](img/image8.png)

В случае, если аутентификация пройдена, будет выведен список адресов и
предложено ввести порядковый номер ссылки, которую нужно будет
отредактировать:

![](img/image9.png)

При выборе ссылке будет предложено изменить все пункты, при этом текущие
значения будут доступны как значения по-умолчанию:

![](img/image10.png)

Как видим, значение ссылки изменилось:

![](img/image11.png)

Через некоторое время после окончания времени действия, ссылка
автоматически удалится (5 минут):

![](img/image12.png)

5.  Удаление ссылки -- при выборе выводится такой же список, как и при
    редактировании, нужная ссылка удаляется:

![](img/image13.png)

После чего указанная ссылка удаляется из списка:

![](img/image14.png)

6.  При первом запуске создаётся файл config.json, куда сохраняются
    настройки по-умолчанию, при желании можно их изменить. Настройка
    delete_expired_after_minutes = 0 означает, что удаления ссылок
    после истечения времени не будет происходить.
