/* Initial beliefs and rules */

/* Initial goals */

/* Plans */

+!initRoot(N)<-
	+iam(root);
	+isempty;
	+index(N);
	+child(N*2);
	+child(N*2+1).

+!initNode(N) <-
	+iam(node);
	+index(N);
	+isempty;
	+parent(math.floor(N/2));
	+child(N*2);
	+child(N*2+1).
	//.print("I am node.").

+!initLeaf(N) <-	
	+parent(math.floor(N/2));
	+index(N);
	.random(R);   
	+iam(leaf);
	+number(math.floor(R * 100));
	.print("Generated: ", math.floor(R * 100)).

//+!print_number: number(N) <- .print(N).

@a +!step: parent(P) & number(N) & index(I) & (iam(node) | iam(root)) <-  
	.random(R);
	.wait(R*100);
	.concat("agent", P, X);
	//.print("I am agent", I, " and Im sending up to ", X);
	.send(X, achieve, save(N, I)).
	
@b +!step <- .wait(1).

@c +!save(N, I): iam(node) | iam(root)<-
	if(I==14 | I==12 | I==10 | I==8 | I==6 | I==4 | I==2 ){
		+first(N);
		//.print("Got first number ", N, " from agent", I);
	} else {
		+second(N);
		//.print("Got 2nd number ", N, " from agent", I);
	};
	if(first(L) & second(R) & isempty){
		//.print("GOT BOTH ", R, L);
		-isempty;
		!getMin(R, L)
	}.

@d +!getMin(R, L): index(I) <- 
	//.print(math.min(R, L));
	+number(math.min(R, L));
	if(math.min(R, L) == L){
		//.print("Left won, clearing ", I*2);
		.concat("agent", I*2, Name);
		.send(Name, achieve, clear(I*2))
	}else{
		//.print("Right won, clearing ", I*2+1);
		.concat("agent", (I*2)+1, Name);
		.send(Name, achieve, clear((I*2)+1))
	};
	if(iam(root)){
		.print("RESULT ", math.min(R, L));
		.send(Name, achieve, clear(I));
		//.send(manager, achieve, sendBC)
	}.                                                                                                                                                                                                                                            
	

@e +!clear(I): number(N)<-
	.print("Clearing ", I);
	-number(N);
	-first;
	-second;
	+isempty.
	
@f +!clear(I) <-
	+isempty.	



	



	
