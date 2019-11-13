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
	
	
@a +!step: index(I) & (iam(node) | iam(root)) <-  	//for all non leafs
	.random(R);
	.wait(R*1+I*10);
	.concat("agent", I*2, X);
	.concat("agent", I*2+1, X2);
	if(iam(root) & number(N)){
		.send("agent1", achieve, clear(I));
		.send("manager", achieve, printResult(N));
	}else{
		.send(X, askOne, isempty, A);
		//.print("My left child : ", X ," got ", A);
		.send(X2, askOne, isempty, B);
		//.print("My right child : ", X2 ," got ", B);
		if(A>false & B>false){
			//.print("both children empty, doin nothing");
			.wait(1)
		}else{			
			if(isempty){
				if(A==false & B>false){				//if only left is not empty
					//.print("only my left child has number");
					.send(X, askOne, number(N), number(MyNum));
					+number(MyNum);
					-isempty;
					.send(X, achieve, clear(I*2));
				}else{
					if(A>false & B==false){					//if only right is not empty
						//.print("only my right child has number");
						.send(X2, askOne, number(N), number(MyNum));
						+number(MyNum);
						-isempty;
						.send(X2, achieve, clear(I*2+1));
					}else{
						if(A==false & B==false){
							.send(X, askOne, number(RE), number(LL));
							.send(X2, askOne, number(RE2), number(RR));
							+first(LL);
							+second(RR);
							+number(math.min(LL, RR));
							-isempty;
							//.print("Both got numbers: ", LL, " ", RR, " min is ", math.min(LL, RR));
							if(math.min(RR, LL) == LL){
								.send(X, achieve, clear(I*2));
								-first(LL);
								-second(RR)
							}else{
								.send(X2, achieve, clear((I*2)+1));
								-first(LL);
								-second(RR)
							};
						}else{
							//.print("CONDITION ERROR ", A, B)
						}
					}
				}
			}else{
				//.print("Im not empty, doin nothing")
			}
		}		
	}.
	
	
@b +!step <- .wait(1).                                                                                                                                                                                               

@e +!clear(I): number(N)<-
	//.print("Clearing ", I);
	-number(N);
	-first;
	-second;
	+isempty.
	
@f +!clear(I) <-
	+isempty.	



	



	
