-- Basic Rollers

insert into rollers ( groupName, rollerName, definition ) values ( 'D4s', '1 D4', '1D4' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D4s', '2 D4s', '2D4' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D4s', '3 D4s', '3D4' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D4s', '4 D4s', '4D4' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D4s', '5 D4s', '5D4' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D4s', '6 D4s', '6D4' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D4s', '7 D4s', '7D4' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D4s', '8 D4s', '8D4' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D4s', '9 D4s', '9D4' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D4s', '10 D4s', '10D4' );

insert into rollers ( groupName, rollerName, definition ) values ( 'D6s', '1 D6', '1D6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D6s', '2 D6s', '2D6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D6s', '3 D6s', '3D6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D6s', '4 D6s', '4D6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D6s', '5 D6s', '5D6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D6s', '6 D6s', '6D6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D6s', '7 D6s', '7D6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D6s', '8 D6s', '8D6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D6s', '9 D6s', '9D6' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D6s', '10 D6s', '10D6' );

insert into rollers ( groupName, rollerName, definition ) values ( 'D8s', '1 D8', '1D8' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D8s', '2 D8s', '2D8' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D8s', '3 D8s', '3D8' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D8s', '4 D8s', '4D8' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D8s', '5 D8s', '5D8' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D8s', '6 D8s', '6D8' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D8s', '7 D8s', '7D8' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D8s', '8 D8s', '8D8' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D8s', '9 D8s', '9D8' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D8s', '10 D8s', '10D8' );

insert into rollers ( groupName, rollerName, definition ) values ( 'D10s', '1 D10', '1D10' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D10s', '2 D10s', '2D10' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D10s', '3 D10s', '3D10' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D10s', '4 D10s', '4D10' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D10s', '5 D10s', '5D10' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D10s', '6 D10s', '6D10' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D10s', '7 D10s', '7D10' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D10s', '8 D10s', '8D10' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D10s', '9 D10s', '9D10' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D10s', '10 D10s', '10D10' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D10s', 'Percentiles (0..99)', 'd100 - 1' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D10s', 'Percentiles (1..100)', 'd100' );

insert into rollers ( groupName, rollerName, definition ) values ( 'D12s', '1 D12', '1D12' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D12s', '2 D12s', '2D12' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D12s', '3 D12s', '3D12' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D12s', '4 D12s', '4D12' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D12s', '5 D12s', '5D12' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D12s', '6 D12s', '6D12' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D12s', '7 D12s', '7D12' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D12s', '8 D12s', '8D12' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D12s', '9 D12s', '9D12' );
insert into rollers ( groupName, rollerName, definition ) values ( 'D12s', '10 D12s', '10D12' );

-- Miscellaneous Rollers

insert into rollers ( groupName, rollerName, definition ) values ( 'Miscellaneous', 'Coin Flip', 'd2' );
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
