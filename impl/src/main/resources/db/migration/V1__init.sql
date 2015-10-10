CREATE TABLE preferences (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    principal_id VARCHAR(255) NOT NULL,
    talk_id VARCHAR(16) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    primary key (id)
);

ALTER TABLE preferences ADD CONSTRAINT unique_principal_talk UNIQUE (principal_id, talk_id);


create table filters (id bigint generated by default as identity, favourites boolean not null, principal_id varchar(255) not null, primary key (id));
create table filters_languages (filters_id bigint not null, languages varchar(255));
create table filters_levels (filters_id bigint not null, levels varchar(255));
create table filters_locations (filters_id bigint not null, locations varchar(255));
create table filters_tracks (filters_id bigint not null, tracks varchar(255));

ALTER TABLE filters ADD CONSTRAINT unique_principal UNIQUE (principal_id);
alter table filters_languages add constraint FK_no38tkt9tkt20olsiiea97xgt foreign key (filters_id) references filters;
alter table filters_levels add constraint FK_hpux1kw86t18r6jqw7aqbjfdp foreign key (filters_id) references filters;
alter table filters_locations add constraint FK_99duwd0nc7ajiip1lmms7orn3 foreign key (filters_id) references filters;
alter table filters_tracks add constraint FK_5lbd2rgl192eej942l5cwaxgh foreign key (filters_id) references filters;
