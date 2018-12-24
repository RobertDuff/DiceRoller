pragma foreign_keys = on;

create table groups
(
	id integer primary key,
	name text not null unique,
	sequence integer
);

create trigger applySequenceToNewGroup after insert on groups
when new.sequence is null
begin
	update groups set sequence = ( select ifnull ( max ( sequence ), 0 ) + 1 from groups ) where id=new.id;
end;

create trigger resequenceGroupsOnDelete after delete on groups
begin
	update groups set sequence = sequence - 1 where sequence > old.sequence;
end;

create table rollers
(
	id integer primary key,
	groupId integer references groups ( id ) on delete cascade,
	Name text not null,
	definition text not null,
	sequence integer,
	unique ( groupId, name )
);

create trigger applySquenceToNewRoller after insert on rollers
when new.sequence is null
begin
	update rollers set sequence = ( select ifnull ( max ( sequence ), 0 ) + 1 from rollers where groupId=new.groupId ) where id=new.id;
end;

create trigger resequenceRollersOnDelete after delete on rollers
begin
	update rollers set sequence = sequence - 1 where groupId = old.groupId and sequence > old.sequence;
end;

create table labels
(
	rollerId integer references rollers ( id ) on delete cascade,
	value integer not null,
	label text not null,
	unique ( rollerId, value )
);

create table triggers
(
	rollerId integer references rollers ( id ) on delete cascade,
	name text not null,
	definition text not null,
	unique ( rollerId, name )
);

create table variables
(
	groupId  references groups ( id ) on delete cascade,
	name text not null,
	value integer not null,
	sequence integer,
	unique ( groupId, name )
);
