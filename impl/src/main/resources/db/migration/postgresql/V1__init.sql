create table favorites (id bigint not null, event_id varchar(255) not null, principal_id varchar(255) not null, version int4, primary key (id));
create table filters (id bigint not null, only_favorites boolean, principal_id varchar(255) not null, primary key (id));
create table filters_languages (filters_id bigint not null, languages varchar(255));
create table filters_levels (filters_id bigint not null, levels varchar(255));
create table filters_locations (filters_id bigint not null, locations varchar(255));
create table filters_tracks (filters_id bigint not null, tracks varchar(255));
alter table favorites add constraint UK_1whak4rghbe1qjjxye3xmbg8j unique (principal_id, event_id);
alter table filters add constraint UK_c0ueuncd985n3x0djdr25j431 unique (principal_id);
alter table filters_languages add constraint FK_gyvmwtcb5t1ffcib3u14ypmdk foreign key (filters_id) references filters;
alter table filters_levels add constraint FK_dy28tlci377xqsgosftuce6li foreign key (filters_id) references filters;
alter table filters_locations add constraint FK_dv4jhsj5ot1jpic6yedya1770 foreign key (filters_id) references filters;
alter table filters_tracks add constraint FK_e4jnka1m8nnq02h3refgwjic2 foreign key (filters_id) references filters;
create sequence if not exists hibernate_sequence;
