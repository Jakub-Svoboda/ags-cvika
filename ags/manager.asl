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

+!execute <-
	.wait(1000);
	.broadcast(achieve, step);
	.wait(3000);
	.print("---------------------");
	.broadcast(achieve, step).
	
+!sendBC <-
	.wait(3000);
	.print("---------------------");
	.broadcast(achieve, sendUp).
	


