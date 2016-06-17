import java.util.Random;


public class Deck {
	
	private Card[] myCards; //The array of cards in the deck, where the top card is in the first index
	private int numCards; //The number of cards currently in the deck.
	
	public Deck() {
		//call the other constructor defining one deck without shuffling
		//overloading deck method ( depending on number and type of parameters )
		this(false);
	}

	public Deck(boolean shuffle) { //shuffle: whether to shuffle the cards
		
		this.numCards = 32;
		this.myCards = new Card[this.numCards];
		
		//card index
		int c = 0;
		int key = 0;
		//create all the cards (each deck 4 suits, 8 types per suit = 32)	
		for (int s = 0; s < 4; s++) { //for each suit
				
			//for each number
			for (int n = 7; n <= 14; n++) { //7 = 7, 8 = 8, ... , 14 = ace
											//key for suit = 0-7 , 8-15, 16-23, 24-31		
				this.myCards[c] = new Card(Suit.values()[s], n, key);	//add a new card to the deck (suit, number, key)
				c++;
				key++;
			}
		}
		
		// shuffle, if true (always in normal game)
		if(shuffle) {
			this.shuffle();	
		}
	}

	/*
	 * Shuffle deck by randomly swapping pairs of cards.
	 */
	public void shuffle() {
		
		//random number generator
		Random rng = new Random();
		
		// Temporary card
		Card temp;
		
		int j;
		for (int i = 0; i < this.numCards; i++){

			//get a random card j to swap i's value with
			j = rng.nextInt(this.numCards);
			
			// do swap
			temp = this.myCards[i];
			this.myCards[i] = this.myCards[j];
			this.myCards[j] = temp;
		}
	
	}

	/*
	 * Deal the next card from the top of the deck.
	 * return the dealt card
	 */
	public Card dealNextCard() {
		
		//get the top card
		Card top = this.myCards[0];
		
		// shift all the subsequent cards to the left by one index
		for(int c = 1; c < this.numCards; c++){
			this.myCards[c-1] = this.myCards[c]; 
		}
		this.myCards[this.numCards-1] = null;
		
		//decrement the number of cards in deck
		this.numCards--;
		return top;
	}
	
	/*
	 * Print the top cards in the deck.
	 * numToPrint:	the number of cards from the top of the deck to print
	 */
	public void printDeck(int numToPrint){
		for(int c = 0; c < numToPrint; c++){
			System.out.printf("% 3d/%d %s\n", c+1, this.numCards, this.myCards[c].toString());
		}
		System.out.printf("\t[%d others]\n", this.numCards-numToPrint);
		
	}
}
