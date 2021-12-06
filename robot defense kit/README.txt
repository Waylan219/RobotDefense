1 A. 
    We looked throught the files to see where the actions are being created. We found
    out that the actions were being created with 8 different directions and 1 power
    setting and being put into a variable called potentials. Here we added an action
    to be able to set the power to 1 (off) and just point it a random direction 
    since it was off and didnt matter. We then loaded that into the end of potentials
    so that it can use that action. We also implemented that if 6 ticks pass by we would
    force a new action consideration.

1 B.
    With the way we approached 1 A we changed the static function to include all 32 
    possible actions with all 4 power settings and all 8 directions. That way it can 
    turn off and consider power settings for each direction. We then realized that 
    is a lot of learning it would have to do for all of those settings. So we then
    changed it to only represent power setting 2 and 4 with those directions and as
    stated in 1 A we added a power off action at the end. 

1 C.
    Big Brainers performance
    511646	36	35	151

    Base Learners performance
    366376	38	17	112

    I feel as if we would have been able to let our agent to run longer it would out
    performan the base learner by even more than what it would have. It would have
    learned mroe of when to turn the vacuum off to save resources and therefore run
    for even longer than the base agent. The base learner would still be using max
    resources non stop and would not perform as well as ours would.

1 D.
    We first assigned the new reward action value to 20 instead of 10 to see if that 
    would change the outcome. We then ran two tests on changing the bug values as we
    noticed at first that it had a hard time trying to catch certain bugs. We did one 
    test with all of the values being 10 and then another test where we reversed the 
    base values in order. 

    All being set to 10 :
    1600059	3	53	106

    Being set in reverse order :
    scarabug += 100
	scarlite += 10
	sqworm += 1

    1636758	5	58	107

    We tested this to see how the differing values for the bugs would effect the output.
    As you can see it did slightly better when the values were different, however this
    can be due to randomness. But as we watched both of them play out we realized that 
    with different values that over time it would run better as at the start it was 
    hardly capturing any bugs but in the end was capturing a decent amount. We then reverted
    back the bugs numbers and we changed the attempts value to increment by 3 instead of 1.

    Attempts incremented by 3:
    44 119

    Attempts incremented by 1:
    50 107 

    We can see that if we keep it by 1 we get better results.
    We then changed back the incrementation value and changed the radius to 1 to see how
    it would perform. 

    Radius 1 on simple1.dat map:
    98 68

    Radius 1 on simple-4pack.det map:
    57 127

    Decided to have fun and see what the results would be if we gave it 100,000 resources. 
    Took quite a while to run but this was the results.

    9745788     35	2414    1368

    We concluded on settling with Radius on 1 and we stayed with a reverse bug order values.

2 A.
    To be able to run the last action that the agent took we passed the last action to the
    reward best action function. We then nested an if statement in the if statement that 
    checks how many possible "best" actions there is. If lastaction is a part of that group
    then it would repeat last action until a better action is available. This did not make a 
    noticable imrpovement on the captured scores. 

    793808	33	119	186
 
2 B.
    Updated by rewarding current state when last state caught a bug. Results were nice
    
    752055	19	150	169

3 A.
    We rewarded related states directions with the same power the reward value / 4. 
    For example if a bug was caught in east it would get rewarded with that same power
    in southeast and northeast for that value / 4. 

3 B.
    We added a class to get a emptyradius boolean variable and if that was true 
    then we would shut off power to be able to save resources.

    Run tests with and without!











