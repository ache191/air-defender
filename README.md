# air-defender

README

http://www.cokeandcode.com/info/tut2d.html
During writing this app, i've used this tutorial, it helped me a lot. 
I've took an idea and method how to work with accelerated graphics with Java + game concept. 

------------ HOW TO RUN ----------
The best way to build it self will be to use maven tool
This is an executable jar file, so please use (or any analog)
java -jar path_to_file

This code was tested on java 1.8 but shoukd be fully compartible with 1.7, unfortunately it is not suppotring 1.6 or lower.
----------------------------------

Changes made by me:
-- player ship moves in any direction
-- pause function
-- persistent highscore table
-- enemies are also fighting with player
-- player have 5 lifes instead of one
-- HUD with score and lifes
-- restart game feature
-- separated all game logic from one god class to correspondent "logic mediators". Refactoring was quite huge, based on my assumption only 30% tutorial here left.
-- simple unit test technique (application itself is not covered, but i've showed the way how to do this)
-- extended explanation in comments
