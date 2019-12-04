import jade.core.*; 
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;

public class Participant extends Agent {
    protected void setup() {
        System.out.println(getLocalName() + " is ready.");	
	try{
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setName("doMyProject");
            sd.setType("doMyProject");
            sd.addOntologies("doMyProject");
            sd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
            //sd.addProperties(new Property("country", "Italy"));
            dfd.addServices(sd);
            System.out.println(getLocalName() + " has added service.");
            
            DFService.register(this, dfd);	            	
        }
        catch (FIPAException fe){
            fe.printStackTrace();
            System.out.println(getLocalName() + " something failed in setup and I dont even know what.");
        }
        
        addBehaviour(new PABehaviour());    //start waiting for messages
    } 
    
    class PABehaviour extends CyclicBehaviour{
    
        @Override
        public void action(){
            ACLMessage message = myAgent.receive();     //see what messages arrived
            if(message == null){            //if none
                block();
            }else{                          //if I got some
                if(message.getPerformative() == ACLMessage.CFP){  //received CFP
                    handleCFP(message);
                }else if(message.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){  //received ACCEPT
                    handleAccept(message);
                }else if(message.getPerformative() == ACLMessage.REJECT_PROPOSAL){  //received DENIAL
                    handleReject(message);
                }else{
                    System.out.println("Received unexpected messsage type. This should not have happened.");
                }
            }
        }

        public void handleCFP(ACLMessage message){
            double choice = Math.random();
            if(choice < 0.1){           //I will send a refusal
                ACLMessage response = new ACLMessage(ACLMessage.REFUSE);      
                response.addReceiver(message.getSender());      //send back to karel
                System.out.println(myAgent.getAID().getName() +" I dont have the time to do this.");
                myAgent.send(response);
            }else if (choice <0.8){     //I will send a proposal
                ACLMessage response = new ACLMessage(ACLMessage.PROPOSE);      
                response.addReceiver(message.getSender());      //send back to karel
                int price = (int) (Math.random()*100);            //convert float>int>string because java is java    
                response.setContent(String.valueOf(price));     //save response
                myAgent.send(response);                         //send it 
                System.out.println(myAgent.getAID().getName() +"I will code it for " + price + ", what do you say?");
            }else if (choice <0.9){     // I will reply late
                AID karel = message.getSender();        //save karel's ID to cache is case new messages came
                int delay = 500 + Integer.parseInt(message.getContent());   //add 0.5 sec to timeout received in message
                myAgent.addBehaviour(new WakerBehaviour(myAgent, delay) {   //and go to sleep
                @Override
                protected void handleElapsedTimeout() {
                    ACLMessage response =  new ACLMessage(ACLMessage.PROPOSE); //creatw proposal
                    response.addReceiver(karel);            //receiver is from cache
                    int price = (int) (Math.random()*100);            //convert float>int>string because java is java    
                    response.setContent(String.valueOf(price));     //save response
                    myAgent.send(response);                         //send it 
                    System.out.println(myAgent.getAID().getName() +"I will code it for " + price + ", but it's too late :(");                
                }
                });
            }else{
                System.out.println(myAgent.getAID().getName() +"I will not even reply to this guy.");
            }

        }
        
        public void handleAccept(ACLMessage message){
            System.out.println("I won, coding now...");
            myAgent.addBehaviour(new WakerBehaviour(myAgent, 5000) {   //and go to sleep
                @Override
                protected void handleElapsedTimeout() {
                    ACLMessage response =  new ACLMessage(ACLMessage.INFORM); //hand in to karel
                    response.addReceiver(message.getSender());            //receiver is from cache  
                    response.setContent("Here is your code, pleasure doin bussiness with you.");     //save response
                    send(response);                         //send it 
                    System.out.println("I'm done, code has been sent.");
                }
            });

        }
        public void handleReject(ACLMessage message){
            System.out.println(myAgent.getAID().getName() +" My offer was refused. ");
        }
    }
    
    
}


