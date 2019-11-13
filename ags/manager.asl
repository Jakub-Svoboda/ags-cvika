// Agent manager in project ags.mas2j

/* Initial beliefs and rules */

/* Initial goals */

!init.
!execute.


/* Plans */

+!init <-
	.send("agent1", achieve, initRoot(1));
	for( .range(I, 2, 7) ){
		.concat("agent", I, N);
		.send(N, achieve, initNode(I));
	};
	for( .range(I, 8, 15) ){
		.concat("agent", I, N);
		.send(N, achieve, initLeaf(I));
	}.

+!printResult(N) <-
	.print(N).
	
+!execute <-       
	for(.range(I, 1, 20) ){
		.wait(1000);
		.broadcast(achieve, step)
	}.
	     




