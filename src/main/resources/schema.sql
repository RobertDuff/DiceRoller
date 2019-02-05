pragma foreign_keys = on;

create table suites
	( 
		id integer primary key, 
		name text not null unique
	);

create table variables
	( 
		id integer primary key,
		suiteId references suites ( id ) on delete cascade, 
		name text not null, 
		value integer not null default 0, 
		sequence integer,
		unique ( suiteId, name )
	);

create trigger applySquenceToNewVariable after insert on variables
when new.sequence is null
begin
	update variables set sequence = ( select ifnull ( max ( sequence ), 0 ) + 1 from variables where suiteId=new.suiteId ) where id=new.id;
end;

create trigger resequenceVariablesOnDelete after delete on variables
begin
	update variables set sequence = sequence - 1 where suiteId = old.suiteId and sequence > old.sequence;
end;

create table triggers 
	( 
		id integer primary key,
		suiteId integer references suites ( id ) on delete cascade, 
		name text not null, 
		definition text not null default "0", 
		unique ( suiteId, name )
	);

create table groups 
	(
		id integer primary key, 
		suiteId integer references suites ( id ) on delete cascade, 
		name text not null, 
		sequence integer, 
		unique ( suiteId, name )
	);

create trigger applySequenceToNewGroup after insert on groups
when new.sequence is null
begin
	update groups set sequence = ( select ifnull ( max ( sequence ), 0 ) + 1 from groups where suiteId=new.suiteId ) where id=new.id;
end;

create trigger resequenceGroupsOnDelete after delete on groups
begin
	update groups set sequence = sequence - 1 where suiteId = old.suiteId and sequence > old.sequence;
end;

create table rollers
	(
		id integer primary key, 
		groupId integer references groups ( id ) on delete cascade, 
		name text not null, 
		definition text not null default "0", 
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
	
create table rollerTriggers
(
	rollerId integer references rollers ( id ) on delete cascade,
	triggerId integer references triggers ( id ) on delete cascade,
	unique ( rollerId, triggerId )
);

create trigger assertRollerAndTriggerAreInSameSuite before insert on rollerTriggers
when ( select suiteId from triggers where id == new.triggerId ) !=
     ( select suiteId from groups where id == ( select groupid from rollers where id == new.rollerId ) )
begin
	select raise ( ABORT, "Roller and Trigger are not in the same Suite" );
end;