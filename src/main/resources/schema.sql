pragma foreign_keys = on;

create table rollers
(
	id integer primary key,
	groupName text not null,
	rollerName text not null,
	definition text not null,
	sequence integer,
	unique ( groupName, rollerName )
);

create trigger applySquenceToNewRoller after insert on rollers
when new.sequence is null
begin
	update rollers set sequence=( select ifnull ( max ( sequence ), 0 ) + 1 from rollers ) where id=NEW.id;
end;

create table rollerLabels
(
	rollerId integer not null references rollers ( id ) on delete cascade,
	value integer not null,
	label text not null
);

create table rollerTriggers
(
	rollerId integer not null references rollers ( id ) on delete cascade,
	triggerName text not null,
	definition text not null
);

create table variables
(
	groupName text not null,
	variableName text not null,
	value integer not null,
	unique ( groupName, variableName )
);
