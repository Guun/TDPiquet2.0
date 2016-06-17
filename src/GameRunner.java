
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Collections;
import java.util.Vector;





//import java.util.Vector;
//
//import org.encog.neural.data.NeuralData;
//import org.encog.neural.data.NeuralDataPair;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.DynamicBackPropagation;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.TransferFunctionType;

public class GameRunner {
	public static void main(String[] args){
		
		ArrayList<Integer> allInputs = new ArrayList<Integer>();
		
		for(int game = 0; game < 100; game++){
			
			 ArrayList<Integer> gameInputs = playGame();	//store resulting inputs from playing a game
			 for(int i : gameInputs){						//add each input from that one game to the arraylist of all inputs
				 allInputs.add(i);
			 }
		}
	}
	
	static ArrayList<Integer> playGame(){
		ArrayList<Integer> inputs = new ArrayList<Integer>();
		
		playRound(); //TODO IMPORTANT: MUST PLAY SIX ROUNDS IN A GAME OF PIQUET (Player changes role each round eg: was elderHand => now youngerHand)
		
		return inputs;
	}

	private static void playRound() {
		// TODO Auto-generated method stub
		Deck theDeck = new Deck(true);
		Player youngerHand = new Player("youngerHand");
		Player elderHand = new Player("elderHand");
		Player talon = new Player("Talon");
		Player yRemovedCards = new Player("yRemovedCards");
		Player eRemovedCards = new Player("eRemovedCards");
		
		for(int i = 0; i < 12; i++ ){	//deal 12 cards each to players
			youngerHand.addCard(theDeck.dealNextCard());
			elderHand.addCard(theDeck.dealNextCard());
		}
		for(int i = 0; i < 8; i++){		//deal 8 remaining cards to talon
			talon.addCard(theDeck.dealNextCard());
		}
		
		ArrayList<Integer> youngerKeys = getKeyList(youngerHand);
		ArrayList<Integer> elderKeys = getKeyList(elderHand);
		
		exchangePhase(youngerHand, elderHand, talon, yRemovedCards, eRemovedCards);
		
		
		
		//printHands(elderHand, youngerHand, talon);
	}
	
	
	private static void exchangePhase(Player youngerHand, Player elderHand, Player talon, Player yRemovedCards, Player eRemovedCards) {
		final int MAX_EXCHANGES = 5;
		Random rand = new Random();
		int exchanges = rand.nextInt(MAX_EXCHANGES) + 1; //must be at least 1:  ((0 >= exchanges < 5 ) + 1)  therefore ( 1 <= exchanges <= 5 )
		int randCard = rand.nextInt(12); //position of card in the elder's hand that will soon be replaced
		int randTalon = rand.nextInt(talon.hand.size()); //random card from talon that will replace one of the elder's cards
		
		ArrayList<Integer> ePrevPositions = new ArrayList<Integer>();
		ePrevPositions.add(randCard);
		while(exchanges > 0){
		
			System.out.print("\nTalon card (Pos:" +randTalon+ "):" +talon.hand.get(randTalon));
			System.out.print(" will replace card at position " +randCard+ " of the elder's hand (" +elderHand.hand.get(randCard) +" ) \n");
			System.out.println("Elder Exchanges left: " + (exchanges-1));
			//System.out.println("Elder hand card being removed: "+ elderHand.hand.get(randCard) +"\n");
			
			eRemovedCards.hand.add(elderHand.hand.get(randCard));
		
			elderHand.hand.set(randCard, talon.hand.get(randTalon));
			talon.hand.remove(randTalon);
			if(talon.hand.size() > 0){
				
				randTalon = rand.nextInt(talon.hand.size());
			}
			
			for(int i = 0; i < ePrevPositions.size(); i++){ //Prevent exchanging card obtained from Talon.
				if(randCard == ePrevPositions.get(i)){
					System.out.println("Attempted new pos 'randCard'("+randCard+") clashes with prevPosition[" +i+ "]: "+ePrevPositions.get(i));
					randCard = rand.nextInt(12);
					i = 0;
					System.out.println("Got new attempted new pos 'randCard' : "+randCard);
				}
			}
			exchanges--;
		}
		
		//eRemovedCards.printHand();
		elderHand.printHand();
		System.out.print("\nElderHand's removed cards after all exchanges: \n");
		for(int c = 0; c < eRemovedCards.hand.size(); c++)
		{
			System.out.printf(c + ".  " + " %s\n",eRemovedCards.hand.get(c).toString());
		}
		System.out.println();
		
	}

	@SuppressWarnings("unused")
	private static void printHands(Player elderHand, Player youngerHand, Player talon) {
		elderHand.printHand();
		youngerHand.printHand();
		talon.printHand();
		
	}

	private static ArrayList<Integer> getKeyList(Player playerHand) {	//create and return arrayList containing card key values (in ascending order) associated with the cards in a player's hand

		//Store keys associated with cards in each player's hands
		ArrayList<Integer> playerKeys = new ArrayList<Integer>();
		for(int i = 0; i < 12; i++){
			playerKeys.add(playerHand.hand.get(i).getKey());	
		}
		
		Collections.sort(playerKeys);	//sort these keys into ascending order
		return playerKeys;
		
	}

	/*
	 * Takes player scores and gives set array of outputs based on score difference
	 * TODO Re-think how to do this later to allow for better differentiation of outputs based on score difference. Having more outputs should make it clearer
	 * TODO Possible idea of how to implement above: Implement like binary system i.e: Output[0] represents(2^0) 0 or 1, Output[1] represents(2^1) 0 or 2 etc
	 * so represent a score difference in binary
	 * turn int into binary string via Integer.toBinaryString(int i) and parse that string into array of outputs similar to below
	 * eg: for unrealistic score difference of 109:
	 * 			outputs[0] = 1.0;	//2^0 = 1
				outputs[1] = 0.0;	
				outputs[2] = 1.0;	//2^2 = 4
				outputs[3] = 1.0;	//2^3 = 8
				outputs[4] = 0.0;
				outputs[5] = 0.0;	//2^5 = 32
				outputs[6] = 1.0;	//2^6 = 64
									//sum = 64+32+8+4+1 = 109
	 */
	double[] getOutputs(double player1Score, double player2Score){
		
		double[] outputs = new double[5];
		if(player1Score > player2Score){
			System.out.println("Player1 wins!\t" +player1Score + " - " + player2Score);
			
			if(player1Score - player2Score > 10){ //p1 won by over 10
				outputs[0] = 1.0;
				outputs[1] = 0.0;
				outputs[2] = 0.0;
				outputs[3] = 0.0;
				outputs[4] = 0.0;
			}
			else{								// p1 won by less than 10
				outputs[0] = 0.0;
				outputs[1] = 1.0;
				outputs[2] = 0.0;
				outputs[3] = 0.0;
				outputs[4] = 0.0;
			}
		}
		else if(player2Score > player1Score){
			System.out.println("Player2 wins!\t" +player1Score + " - " + player2Score);
			if(player2Score - player1Score > 10){ //p2 won by over 10
				outputs[0] = 0.0;
				outputs[1] = 0.0;
				outputs[2] = 0.0;
				outputs[3] = 1.0;
				outputs[4] = 0.0;
			}
			else{								//p2 won by less than 10
				outputs[0] = 0.0;
				outputs[1] = 0.0;
				outputs[2] = 1.0;
				outputs[3] = 0.0;
				outputs[4] = 0.0;
			}
		}
		else if(player1Score == player2Score){ //draw
			System.out.println("Scores Equal : Draw. " +player1Score + " - " + player2Score);
			outputs[0] = 0.0;
			outputs[1] = 0.0;
			outputs[2] = 0.0;
			outputs[3] = 0.0;
			outputs[4] = 1.0;
		}
		for(int i = 0; i <= 4; i++){
			System.out.println(outputs[i]);
		}
		
		return outputs;
	}
	
	
}
