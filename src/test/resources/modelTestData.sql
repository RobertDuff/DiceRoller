-- Data for Testing the Roller Model

insert into groups ( id, name ) values ( 1, "G1" );
insert into groups ( id, name ) values ( 2, "G2" );
insert into groups ( id, name ) values ( 3, "G3" );
insert into groups ( id, name ) values ( 4, "G4" );
insert into groups ( id, name ) values ( 5, "G5" );
insert into groups ( id, name ) values ( 6, "G6" );
insert into groups ( id, name ) values ( 7, "G7" );

insert into rollers ( groupId, id, name, definition ) values ( 1, 1, 'R1', '1D4' );
insert into rollers ( groupId, id, name, definition ) values ( 1, 2, 'R2', '1D4' );
insert into rollers ( groupId, id, name, definition ) values ( 1, 3, 'R3', '1D4' );
insert into rollers ( groupId, id, name, definition ) values ( 1, 4, 'R4', '1D4' );
insert into rollers ( groupId, id, name, definition ) values ( 1, 5, 'R5', '1D4' );
insert into rollers ( groupId, id, name, definition ) values ( 1, 6, 'R6', '1D4' );
insert into rollers ( groupId, id, name, definition ) values ( 1, 7, 'R7', '1D4' );

insert into triggers values ( 1, 'T1', 'A == 1' );
insert into triggers values ( 1, 'T2', 'A == 1' );

insert into labels values ( 1, 1, "One" );
insert into labels values ( 1, 2, "Two" );

insert into variables values ( 1, "V1", 3, 1 );
insert into variables values ( 1, "V2", 6, 2 );
