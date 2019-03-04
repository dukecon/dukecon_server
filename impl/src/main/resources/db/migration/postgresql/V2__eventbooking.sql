create table event_booking (id bigint, event_id varchar(255) not null, conference_id varchar(255) not null, fully_booked boolean, number_occupied int4, version int4, primary key (id));
alter table event_booking add constraint UK_i963nubbo8eo5pc5i6rjygx9a unique (conference_id, event_id);
