import java.util.Arrays;
import java.sql.Timestamp;
import java.io.UnsupportedEncodingException;
public class Block {
    //instance variables
    private int index; // the index of the block in the list
    private Timestamp timestamp; // time at which transaction has been processed
    private Transaction transaction; // the transaction object
    private String nonce; // random string (for proof of work)
    private String previousHash; // previous hash (set to "00000" in first block)
    private String hash; // hash of the block (hash of string obtained from previous variables via toString() method)...
    private int tries; //amount of hash trials to find the nonce
    
    /**
     * constructor with all information (when reading file)
     */
    Block(int index, Transaction transaction, String nonce, String previousHash, String hash, Timestamp timestamp){
        this.index = index;
        this.transaction = transaction;
        this.nonce = nonce;
        this.previousHash = previousHash;
        this.hash = hash;
        this.timestamp = timestamp;
        this.tries = 0;
    }
    /**
     * constructor for a new transaction
     */
    Block(int index, Transaction transaction, String previousHash){
        this.index = index;
        this.transaction = transaction;
        this.previousHash = previousHash;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.tries = this.hash();
        
    }
    /**
     * Finds a nonce and produces the hash
     * @return number of tries taken to find a good nonce
     */
    int hash(){
        this.nonce = genNonce(0); //generates a nonce
        int count = 1;
        try{
            this.hash = Sha1.hash(this.toString());
            while (!hash.substring(0, 5).equals("00000")){ //runs until good nonce is found
                this.nonce = genNonce(count);
                this.hash = Sha1.hash(this.toString());
                count++;
            }
        }
        catch (UnsupportedEncodingException e){
            System.out.println("UnsupportedEncodingException caught");
        }
        return count-1;
        
    }

    /**
     * converts a decimal integer to a string serving as a nonce
     * @param x decimal integer
     * @return nonce
     */
    public static String genNonce(int x){
        String[] characters = new String[94];
        for( var i = 33; i <= 126; i++ ){
            characters[i-33]=String.valueOf((char)(i));
        }
        int remainder = 0;
        String total="";
        if (x==0){
            return "!";
        }
        while(x>0){
            remainder = x%94;
            x=x/94;
            total= characters[remainder] + total;
            
        }
        return total;    
    }

    /**
     * Gets index
     * @return block index
     */
    int getIndex(){
        return this.index;
    }
    /**
     * Gets tries
     * @return number of tries to find a good nonce
     */
    int getTries(){
        return this.tries;
    }
    

    /**
     * Gets timestamp
     * @return timestamp of block creation
     */
    Timestamp getTimestamp(){
        return this.timestamp;
    }
    /**
     * Gets transaction
     * @return transaction in this block
     */
    Transaction getTransaction(){
        return this.transaction;
    }
    /**
     * Gets nonce
     * @return nonce for this block
     */
    String getNonce(){
        return this.nonce;
    }
    /**
     * Gets previous hash
     * @return hash of previous block
     */
    String getPreviousHash(){
        return this.previousHash;
    }
    /**
     * Gets hash
     * @return hash for this block
     */
    String getHash(){
        return this.hash;
    }

    /**
     * String representation of Block
     * @return block as a string
     */
    public String toString() {
    return timestamp.toString() + ":" + transaction.toString()
    + "." + nonce + previousHash;
   }
   }