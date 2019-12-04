import jade.core.*; 
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import java.util.ArrayList;
import java.util.Collections;


public class Initiator extends Agent{
        public DFAgentDescription[] descriptions;   
        public ArrayList<Integer> prices = new ArrayList<>();    //Holds all prices of offers
        public ArrayList<AID> ids = new ArrayList<>();               //Holds the IDs of the offering agent
    
        @Override
    protected void setup() {
        System.out.println(getLocalName() +" is ready.");
        System.out.println(getLocalName() +" is going to sleep.");

        //initial wake behaviour
        addBehaviour(new WakerBehaviour(this, 5000){
            protected void handleElapsedTimeout() {
                System.out.println(getLocalName() +  " woke up and has a project to do." );
                System.out.println(getLocalName() +  ": who can do this project for me?" );
                getPAs("doMyProject");
                int numOfDescriptions = descriptions.length;
                if (numOfDescriptions == 0){
                    System.out.println( "Nobody offered me services?");
                }else{
                    ACLMessage message = new ACLMessage(ACLMessage.CFP);    //create a new FIPA 2000 message
                    for(int i=0; i<numOfDescriptions; i++){
                        message.addReceiver(descriptions[i].getName());     //add each receiver to the message recipient list
                    }
                    message.setContent("10000");        //Send a timeout limit to recipients as a string
                    myAgent.send(message);     
                    addBehaviour(new InitiatorBehaviour()); //Start waiting for da message.
                }
            }
        });
        
        //behaviour for the selection of the winning offer
        addBehaviour(new WakerBehaviour(this, 10000){
            protected void handleElapsedTimeout() {
                System.out.println(getLocalName() +  " It's time to select the winner." );
                if (prices.size() > 2){         //more the 2 offer, all ok
                    int minIndex = prices.indexOf(Collections.min(prices));    //get index of lowest price
                    int maxIndex = prices.indexOf(Collections.max(prices));    //get index of highest price
                    ACLMessage message = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                    message.addReceiver(ids.get(minIndex));
                    message.addReceiver(ids.get(maxIndex));
                    send(message);
                    System.out.println(ids.get(minIndex) + " and " + ids.get(maxIndex) + " has been removed (lowest/highest price).");
                    prices.remove(minIndex);              //remove min and max
                    prices.remove(maxIndex);
                    ids.remove(minIndex);
                    ids.remove(maxIndex);
                    minIndex = prices.indexOf(Collections.min(prices));        //get the (second) lowest offer
                    ACLMessage message2 = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    message2.addReceiver(ids.get(minIndex));
                    send(message2);
                    System.out.println(ids.get(minIndex) + " has won, get to work!"); 
                    ids.remove(minIndex);
                    prices.remove(minIndex);      
                    if(prices.size()>0){
                        ACLMessage message3 = new ACLMessage(ACLMessage.REJECT_PROPOSAL);   //Refuse all the remaining offers
                        for(int i = 0; i < prices.size(); i++){
                            message3.addReceiver(ids.get(i));
                        }
                        send(message3);
                    }    
                   //Receive the code from winner
                    addBehaviour(new InitiatorBehaviour()); 
                    
                }else if(prices.size()> 0){
                    ACLMessage message3 = new ACLMessage(ACLMessage.REJECT_PROPOSAL);   //Refuse all the remaining offers
                    for(int i = 0; i < prices.size(); i++){
                        message3.addReceiver(ids.get(i));
                    }
                    send(message3);
                    System.out.println("Nobody won, I have to do this myself!."); 
                }else{
                    System.out.println("Nobody won, I have to do this myself!.");
                }
            }
        });
        
 
    } 
        
    public void getPAs(String channelName) {
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd  = new ServiceDescription();
        sd.setType(channelName);
        dfd.addServices(sd);
        try{
            this.descriptions =  DFService.search(this, dfd);
            System.out.println(getLocalName() + ": received total " + descriptions.length + " services.");
        }catch (FIPAException e){
            e.printStackTrace();
            System.out.println(getLocalName() + " something failed and I dont even know what.");
        }
    }
     


    class InitiatorBehaviour extends CyclicBehaviour{
         @Override
         public void action(){
            ACLMessage message = myAgent.receive();     //see what messages arrived
            if(message == null){            //if none
                block();
            }else{                          //if I got some
                if(message.getPerformative() == ACLMessage.PROPOSE){  //received a proposal
                    int price = Integer.parseInt(message.getContent());
                    AID sender = message.getSender();
                    System.out.println(myAgent.getAID().getName() + " received a offer for " + price + " from " + sender);
                    prices.add(price);  //append price
                    ids.add(sender);    //append sender
                }else if(message.getPerformative() == ACLMessage.REFUSE){  //received a refusal
                    System.out.println(myAgent.getAID().getName() + " refused to do the work.");
                }else if(message.getPerformative() == ACLMessage.INFORM){   //received the code    
                    System.out.println("I received the message: " + message.getContent() + " SUCCESS!!!");
                }else{
                    System.out.println("Received unexpected messsage type. This should not have happened.");
                }
            }
         }
    }
}