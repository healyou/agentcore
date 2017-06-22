------------ news ------------
create table if not exists data.news (
    id			    bigserial primary key,
    user_id		  bigint not null,
    headline    varchar(200) not null,
    text        text,
    create_date        timestamp with time zone not null,
    image		    bytea
);

alter table data.news add constraint news_person_fk foreign key (user_id) references data.users(id);

comment on table data.news is 'Новости';
comment on column data.news.id is 'Первичный ключ';
comment on column data.news.headline is 'Заголовок';
comment on column data.news.text is 'Текст';
comment on column data.news.user_id is 'Пользователь';
comment on column data.news.create_date is 'Дата публикации';
comment on column data.news.image is 'Изображение';
