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

    We feel as if we would have been able to let our agent to run longer our agent would out
    perform the base learner by even more than what our agent would have. Our agent would have
    learned more of when to turn the vacuum off to save resources and therefore run
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
    We approached this by looked at how the states are stored in the step function. We can see
    that is uses lastState first and rewards that state, it then sets lastState to the current state
    afterwards. We can then use that to reward the current state that is stored in the lastState
    before it loops again. Therefore we reward our lastState normally as it caught a bug, and if 
    lastState caught a bug we reward the current state aswell but not nearly as heavily. lastState 
    gets rewarded 20 while thisState gets rewarded 5. 
    
    Our results after implementing this :
    752055	19	150	169

3 A.
    We thought of ways to reward related actions to our current action. If a bug was caught on
    power 4 and it was pointing North , we wanted to reward the directions similar to North ; which
    would be North-East and North-West. The way we approached this is be looking at how the actions
    are being stored when we reward them, which is in an array of size 16. We then printed out the array
    to see how they are being stored and realized there was a pattern based off our initial for loop. To 
    be able to get the related state you would have to increment by 2 and decrement by 2. We then tried this
    and got a null pointer error because if the action was stored in position 1 it would try to access
    and action stored in [-1]; which doesn't exist. We then made two if statements to loop back around
    our array so that if the action stored in 0 was called it would reward action at position 2 and
    position 14; which would be the two actions related to our action that we are rewarding. However
    we did not want to reward these actions the same value as the action that caught the bug so we
    rewarded it the normal value / 4, which would be 5. Which is the same value as our current action
    reward implemented from the last step. 

3 B.
    When we get access to change StateVector and CellContent we first thought of how we can return
    when there is nothing in the radius; which is the only time it determines when to take an 
    action based on knowledge. We then created a new public boolean variable called EmptyRadius
    and it would add all of the bugs code together and if that was 0 then that means there is 
    nothing currently in the radius for it to make a knowledge based decision. Therefore we set
    this boolean in the current StateVector to true. We then create a GetEmptyRadius which returns
    this boolean for each state and then we can flip the switch on the vacuum to save resources.
    We also approached this by looking at the insects but that approach did not work for us and 
    decided to keep the approach we had. After this we also turned the radius back up to 2; but 
    that proved less results; which makes sense since it mainly catches bugs when they are beside 
    the vacuum. The reason behind that is because it triggers double the amount of state changes
    and therefore would make more actions. 

    With our agent :

    1173011	1	229	232

    Base agent :
    507676	100	107	105

    As you can see it runs for 665,335 more; which is over double what the base agents run time is!
    The reason being is because it does not go at full throttle and now really implies and saving
    resources by switching it to 0 if the radius is empty. 

    We also tried changing the bugs values to see how that woud impact our results.

    Bugs all valued at 10:
    1066532	47	185	226

    As you can see it did worse, which expectedly so since it applies the same ruling to each
    bug even though they prefer different power levels. 

    We also added to our representation function a Power Off string to represent when the 
    vacuum is being powered off because the radius is empty. 
    


    Can be easily ran if you have make installed by running these commands in order :

    make

    make run

    or for the shorter option : 

    make runshort













