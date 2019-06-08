public class Transaction {
    //instance variables
    private String sender; //sender of transaction
    private String receiver; //receiver of transaction
    private int amount; //amount transfered

    //constructor
    Transaction(String sender, String receiver, int amount){
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }
    
    /**
     * Gets sender
     * @return username of sender
     */
    String getSender(){
        return this.sender;
    }
    /**
     * Gets receiver
     * @return username of receiver
     */
    String getReceiver(){
        return this.receiver;
    }
    /**
     * Gets amount transfered
     * @return amount transfered
     */
    int getAmount(){
        return this.amount;
    }
    /**
     * String representation of transaction
     * @return transaction as a string
     */
    public String toString() {
    return sender + ":" + receiver + "=" + amount;
   }
}