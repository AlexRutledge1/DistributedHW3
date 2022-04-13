# DistributedHW3
 
## Problem 1
To run my solution: javac hw3_linked.java && java hw3_linked

### Proof of Correctness
My solution for the Birthday Presents Party problem is an implementation of the lock-free linked list that we discussed from chapter 9 of the textbook. 
Workers randomly choose to either add or remove a present to the linked list (if both choices are possible), in order for an element to be removed from the list the next element after it must be unmarked and then successfully marked by the attempting thread. Only after a successful removal do we add another 'Thank you' to our output. 
At the end of the process the program will print the number of 'Thank you' cards that they processed. 

### Testing
After extensive testing I found that my solution would either print '500000' or '500001' 

## Problem 2
To run my solution: javac hw3_mars.java && java hw3_mars

### Proof of Correctness
My solution for the Atomospheric Temperature Reading Module was to collect readings from each thread every "minute" (minutes were shrunk to be 50ms quicken testing) and store the reading along with the timestamp of when it was taken. At the end of every "hour" the stored data from each thread were compared and the lists of the highest temperatures, lowest temperatures, and the largest temperature jump in a 10 minute window were calculated by a designated manager.

### Testing
Since the program simulates collecting data with a range of 170 and taking 480 readings per hour, nearly every iteration of execution ended with the lowest and highest lists constaining the lowest and highest possible readings. The largest gap had more variation but consistently produced a low temperture between -100F and -96F and the highest temp stayed between 65F and 70F.