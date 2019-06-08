import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.sql.Timestamp;
import java.io.UnsupportedEncodingException;

public class BlockChain{

    private ArrayList<Block> blockchain = new ArrayList<Block>(); //stores blocks
    private HashMap<String, Integer> bank = new HashMap<String, Integer>(); //keeps track of users' balances
    private ArrayList<Integer> stats = new ArrayList<Integer>(); //keeps track of hash trials for nonce

    BlockChain(){
    }
    /**
     * Reads file into the blockchain
     * @param fileName name of file to read
     * @return blockchain
     */
    public static BlockChain fromFile(String fileName) throws IOException{
        BlockChain blockchain = new BlockChain();
        
        try{
            FileReader reader = new FileReader(fileName);
            BufferedReader buffer = new BufferedReader(reader);
            int index; // the index of the block in the list
            Timestamp timestamp; // time at which transaction has been processed
            String sender; //sender of transaction
            String receiver; //receiver of transaction
            int amount; //amount transfered
            String nonce; // random string (for proof of work)
            String hash;// hash of the block (hash of string obtained from previous variables via toString() method)...
            String previousHash = "00000"; // previous hash (set to "00000" in first block)
            String line;
            while((line = buffer.readLine()) != null) {
                index = Integer.parseInt(line);
                timestamp = new Timestamp(Long.parseLong(buffer.readLine()));
                sender = buffer.readLine();
                receiver = buffer.readLine();
                amount = Integer.parseInt(buffer.readLine());
                nonce = buffer.readLine();
                hash = buffer.readLine();
                blockchain.add(new Block(index, new Transaction(sender, receiver, amount), nonce, previousHash, hash, timestamp));
                previousHash = hash;
                //change balances in bank accordingly
                blockchain.setBalance(receiver, blockchain.getBalance(receiver)+amount);
                blockchain.setBalance(sender, blockchain.getBalance(sender)-amount);

            }  
            buffer.close(); 
        }
        catch (NumberFormatException e){
            System.out.println("invalid file format");
        }
        return blockchain;
    }
    /**
     * Writes blockchain to file
     * @param fileName name of file to write to
     */
    public void toFile(String fileName){
        try {
            FileWriter writer = new FileWriter(fileName);
            BufferedWriter buffer = new BufferedWriter(writer);
            for (int i=0; i<blockchain.size(); i++){ //run for each block in blockchain
                buffer.write(Integer.toString(blockchain.get(i).getIndex())); //writes index
                buffer.newLine();
                buffer.write(Long.toString(blockchain.get(i).getTimestamp().getTime())); //writes time
                buffer.newLine();
                buffer.write(blockchain.get(i).getTransaction().getSender()); //writes sender
                buffer.newLine();
                buffer.write(blockchain.get(i).getTransaction().getReceiver()); //writes receiver
                buffer.newLine();
                buffer.write(Integer.toString(blockchain.get(i).getTransaction().getAmount())); //writes amount
                buffer.newLine();
                buffer.write(blockchain.get(i).getNonce()); //writes nonce
                buffer.newLine();
                buffer.write(blockchain.get(i).getHash()); //writes hash
                buffer.newLine();
            }
            buffer.close();
            writer.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("file not found");               
        }
        catch(IOException ex) {
            System.out.println("IO exception");
        }
    }
    /**
     * Validates blockchain
     * @return true if blockchain is valid, false otherwise
     */
    public boolean validateBlockchain(){
        for (int i = 0; i<blockchain.size(); i++){ 
            try{
                //checks hash is valid
                if (!Sha1.hash(blockchain.get(i).toString()).equals(blockchain.get(i).getHash())){
                    System.out.println("Invalid hash at index: "+i);
                    return false;  
                }
            }
            catch (UnsupportedEncodingException e){
                System.out.println("UnsupportedEncodingException caught");
                return false;

            }
            if(blockchain.get(i).getTransaction().getAmount()<=0){
                System.out.println("Can't transfer negative amounts");
                return false;
            }
            if (i!=0){
                //checks previous hash matches and times are sequential
                if(!blockchain.get(i).getPreviousHash().equals(blockchain.get(i-1).getHash())){
                    System.out.println("Hashes do not match");
                    return false;
                }
                if(blockchain.get(i).getTimestamp().getTime()<blockchain.get(i-1).getTimestamp().getTime()){
                    System.out.println("Timestamps are inconsistent");
                    return false;
                }
            }
            //checks indexes match
            if(i!=blockchain.get(i).getIndex()){
                System.out.println("Indexes do not match");
                return false;
            }
        }
        //creates an array of all balances
        Integer [] nums = new Integer[bank.size()];
        nums = bank.values().toArray(nums);
        
        //checks no balances are negative
        for (int i=0; i<nums.length; i++){
            if (nums[i]<0){
                System.out.println("Negative balance");
                return false;
            }
        }
        return true;
    }
    /**
     * Gets balance of user
     * @param username of user
     * @return balance
     */
    public int getBalance(String username){
        if(!this.bank.containsKey(username)){
            return 0;
        }
        else{
            return this.bank.get(username);
        }
    }
    /**
     * Sets balance of user
     * @param username of user
     * @param amount to set balance to
     */
    public void setBalance(String username, int amount){
        if(!username.equals("bitcoin")){
            bank.put(username, amount);
        }
        
    }
    /**
     * Add a block to the blockchain
     * @param block to add
     */
    public void add(Block block){
        blockchain.add(block);
        //sets balances accordingly
        this.setBalance(block.getTransaction().getSender(), this.getBalance(block.getTransaction().getSender())-block.getTransaction().getAmount());
        this.setBalance(block.getTransaction().getReceiver(), this.getBalance(block.getTransaction().getReceiver())+block.getTransaction().getAmount());
    }

    /**
     * Gets a block given it's index
     * @param index of block
     * @return block
     */
    public Block getBlock(int index){
        return blockchain.get(index);
    }
    /**
     * Gets size of blockchain
     * @return size of blockchain
     */
    public int size(){
        return blockchain.size();
    }
    /**
     * Main function
     * @param args
     */
    public static void main(String[] args){
        Scanner scanner = new Scanner( System.in );
        System.out.print("Enter the name of the blockchain file (not including file extension: ");
        String filename = scanner.next();
        boolean valid = true;
        boolean added = false;
        BlockChain blockchain;
        try{
            blockchain = fromFile(filename+".txt");
            if(!blockchain.validateBlockchain()){
                System.out.println("This blockchain is invalid");
                valid = false;
                }
            else{
                added = true;
                System.out.println("Blockchain is valid");
                System.out.println("Do you want to add another transaction? Enter yes to continue");
                if(!scanner.next().equals("yes")){
                    valid = false;
                }
            }
        }
        catch(IOException e){
            valid = false;
            blockchain = new BlockChain();
            System.out.println("File not found");
        }
        if(added){
        //only runs if blockchain is valid and user wants to add transaction
        while (valid){
            //gets new transaction 
            System.out.print("New blockchain entry:\nEnter the sender: ");
            String sender = scanner.next();
            while(sender.equals("bitcoin")){
                System.out.println("That is not a valid sender. Enter a new sender");
                sender = scanner.next();
            }
            System.out.print("Enter the receiver: ");
            String receiver = scanner.next();
            while(receiver.equals("bitcoin")){
                System.out.println("That is not a valid receiver. Enter a new receiver");
                receiver = scanner.next();
            }
            System.out.print("Enter the amount: ");
            boolean correctamount = false;
            int amount = 0;
            //verifies amount is valid
            while (!correctamount){
                try {
                    String tempamount = scanner.next();
                    int tempamount2 = Integer.parseInt(tempamount);
                    //checks if user has enough balance and amount is greater than 0
                    if((tempamount2 <= blockchain.getBalance(sender))&& (tempamount2 > 0)){
                        amount = tempamount2;
                        correctamount = true; 
                    }
                    else if(tempamount2 > blockchain.getBalance(sender)){
                        System.out.println("Sender does not have enough balance, enter a new amount");
                    }
                    else{
                        System.out.println("Amount not valid, enter a new amount");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("That is not a number. enter an integer amount");
                }
            }
            //Creates new transaction
            Transaction transaction = new Transaction(sender, receiver, amount);

            //sets previousHash to "00000" if first transaction and to previous block's hash otherwise
            String previousHash = "00000";
            if(blockchain.size() != 0){
                previousHash = blockchain.getBlock(blockchain.size()-1).getHash();
            }

            //Creates new block
            System.out.println("\nPlease wait while nonce is found");
            Block block = new Block(blockchain.size(), transaction, previousHash);

            //adds number of tries to stats list
            blockchain.stats.add(block.getTries());
            //adds block to blockchain
            blockchain.add(block);

            System.out.println("Nonce took "+ block.getTries()+" hash trials to find\n");
            System.out.println("Do you want to add another transaction? Enter yes to continue");
            if(!scanner.next().equals("yes")){
                valid = false;
            }
        }
        //generates statistics from stats list
        if(blockchain.stats.size()>0){
            int sum = blockchain.stats.get(0);
            int min=blockchain.stats.get(0);;
            int max = blockchain.stats.get(0);
            for (int i=1; i<blockchain.stats.size();i++){
                if (blockchain.stats.get(i)>max){
                    max = blockchain.stats.get(i);
                }
                if (blockchain.stats.get(i)<min){
                    min = blockchain.stats.get(i);
                }
                sum+=blockchain.stats.get(i);
            }
            int average = sum/blockchain.stats.size();
            //prints statistics
            System.out.println("\nStats:\nMin: "+min+"\nMax: "+max+"\nAverage: "+average);
            for (int i=0; i<blockchain.stats.size();i++){
                System.out.println("Transaction at index "+(i+blockchain.size())+" took: "+blockchain.stats.get(i)+" hash trials");
            }

        } else{
            System.out.println("No transactions added, no stats availible");
        }
        
        //writes blockchain to file
        System.out.println("\nEnter your id (if your uottawa email is ierli042@uottawa.ca, your id is ierli042");
        String id = scanner.next();
        blockchain.toFile(filename+"_"+id+".txt");
    }
        scanner.close();
    }
}