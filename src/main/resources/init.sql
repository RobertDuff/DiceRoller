-- General Rollers

insert into rollers ( groupName, rollerName, definition ) values ( 'General', 'Coin Flip', 'd2' );
insert into rollerLabels values ( ( select id from rollers where rollerName='Coin Flip' ), 1, 'Heads' );
insert into rollerLabels values ( ( select id from rollers where rollerName='Coin Flip' ), 2, 'Tails' );

-- Munchkin

insert into rollers ( groupName, rollerName, definition ) values ( 'Munchkin', 'Escape-1', 'd6-1 >= 5' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Munchkin' and rollerName='Escape-1' ), 1, 'Escape' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Munchkin' and rollerName='Escape-1' ), 0, 'Bad Stuff' );

insert into rollers ( groupName, rollerName, definition ) values ( 'Munchkin', 'Escape', 'd6 >= 5' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Munchkin' and rollerName='Escape' ), 1, 'Escape' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Munchkin' and rollerName='Escape' ), 0, 'Bad Stuff' );

insert into rollers ( groupName, rollerName, definition ) values ( 'Munchkin', 'Escape+1', 'd6+1 >= 5' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Munchkin' and rollerName='Escape+1' ), 1, 'Escape' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Munchkin' and rollerName='Escape+1' ), 0, 'Bad Stuff' );

insert into rollers ( groupName, rollerName, definition ) values ( 'Munchkin', 'Escape+2', 'd6+2 >= 5' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Munchkin' and rollerName='Escape+2' ), 1, 'Escape' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Munchkin' and rollerName='Escape+2' ), 0, 'Bad Stuff' );

insert into rollers ( groupName, rollerName, definition ) values ( 'Munchkin', 'Escape+3', 'd6+3 >= 5' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Munchkin' and rollerName='Escape+3' ), 1, 'Escape' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Munchkin' and rollerName='Escape+3' ), 0, 'Bad Stuff' );

-- Dungeons and Dragons 3.5

insert into rollers ( groupName, rollerName, definition ) values ( 'Dungeons and Dragons 3.5', 'Karma D20', 'd20' );
insert into rollerTriggers values ( ( select id from rollers where groupName = 'Dungeons and Dragons 3.5' and rollerName='Karma D20' ), 'Good Karma', 'A == 20' );
insert into rollerTriggers values ( ( select id from rollers where groupName = 'Dungeons and Dragons 3.5' and rollerName='Karma D20' ), 'Bad Karma', 'A == 1' );

insert into rollers ( groupName, rollerName, definition ) values ( 'Dungeons and Dragons 3.5', 'Good Karma Point', 'd20 >= 17' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Dungeons and Dragons 3.5' and rollerName='Good Karma Point' ), 1, 'Bring On The Good Stuff' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Dungeons and Dragons 3.5' and rollerName='Good Karma Point' ), 0, 'Too Bad' );

insert into rollers ( groupName, rollerName, definition ) values ( 'Dungeons and Dragons 3.5', 'Bad Karma Point', 'd20 <= 4' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Dungeons and Dragons 3.5' and rollerName='Bad Karma Point' ), 1, 'That''s Gonna Sting' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Dungeons and Dragons 3.5' and rollerName='Bad Karma Point' ), 0, 'Wheew' );

insert into rollers ( groupName, rollerName, definition ) values ( 'Dungeons and Dragons 3.5', 'Ability Roll', '3,4d6' );

insert into variables values ( 'Dungeons and Dragons 3.5', 'STR', 17 );
insert into variables values ( 'Dungeons and Dragons 3.5', 'CON', 17 );
insert into variables values ( 'Dungeons and Dragons 3.5', 'DEX', 17 );
insert into variables values ( 'Dungeons and Dragons 3.5', 'INT', 17 );
insert into variables values ( 'Dungeons and Dragons 3.5', 'WIS', 17 );
insert into variables values ( 'Dungeons and Dragons 3.5', 'CHA', 17 );

--
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0', 'Hero 3D6', '3d6' );
insert into rollerTriggers values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Hero 3D6' ), 'Brownie Point', 'A <= 4' );
insert into rollerTriggers values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Hero 3D6' ), 'Sympathy Point', 'A == 18' );

insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0', 'Location', '3d6' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ),  3, 'Eyes' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ),  4, 'Face' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ),  5, 'Head' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ),  6, 'Hands' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ),  7, 'Arms' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ),  8, 'Arms' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ),  9, 'Shoulders' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ), 10, 'Chest' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ), 11, 'Core' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ), 12, 'Stomach' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ), 13, 'Vitals' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ), 14, 'Thighs' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ), 15, 'Legs' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ), 16, 'Legs' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ), 17, 'Feet' );
insert into rollerLabels values ( ( select id from rollers where groupName = 'Hero System 4.0' and rollerName='Location' ), 18, 'Feet' );

--

insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Normal Attacks', 'Normal ATK DC-1', 'd6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Normal Attacks', 'Normal ATK DC-2', '2d6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Normal Attacks', 'Normal ATK DC-3', '3d6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Normal Attacks', 'Normal ATK DC-4', '4d6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Normal Attacks', 'Normal ATK DC-5', '5d6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Normal Attacks', 'Normal ATK DC-6', '6d6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Normal Attacks', 'Normal ATK DC-7', '7d6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Normal Attacks', 'Normal ATK DC-8', '8d6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Normal Attacks', 'Normal ATK DC-9', '9d6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Normal Attacks', 'Normal ATK DC-10', '10d6' );

--

insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Killing Attacks', 'Killing ATK DC-1', 'd2-1' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Killing Attacks', 'Killing ATK DC-2', 'd3' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Killing Attacks', 'Killing ATK DC-3', 'd6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Killing Attacks', 'Killing ATK DC-4', 'd6+1' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Killing Attacks', 'Killing ATK DC-5', 'd6+d3' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Killing Attacks', 'Killing ATK DC-6', '2d6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Killing Attacks', 'Killing ATK DC-7', '2d6+1' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Killing Attacks', 'Killing ATK DC-8', '2d6+d3' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Killing Attacks', 'Killing ATK DC-9', '3d6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'Hero System 4.0 Killing Attacks', 'Killing ATK DC-10', '3d6+1' );

--

-- insert into rollers ( groupName, rollerName, definition ) values ( '', '', '' );
-- insert into rollerLabels values ( ( select id from rollers where groupName = '' and rollerName='' ), , '' );
-- insert into rollerTriggers values ( ( select id from rollers where groupName = '' and rollerName='' ), '', '' );
