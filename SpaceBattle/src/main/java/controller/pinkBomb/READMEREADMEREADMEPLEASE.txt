Add these lines into the BattleTest.java as another case in createPlayer method

/////////////////////////////////////

case "PINK":
    return new PinkBombController(new controller.pinkBomb.GASearch(
            new controller.pinkBomb.UniformCrossover(rnd1),
            new controller.pinkBomb.PMutation(rnd1),
            new controller.pinkBomb.TournamentSelection(rnd1),
            new controller.pinkBomb.RndOpponentGenerator(rnd1),
            rnd1));


/////////////////////////////////////