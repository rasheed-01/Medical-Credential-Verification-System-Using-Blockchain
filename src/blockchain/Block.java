package blockchain;

import java.util.HashMap;
import java.util.List;

public class Block {
    private String uuid;
    private long timestamp;
    private String previousHash;
    private HashMap<String, String> tranxLst;
    private String hash;
    private String merkleRootHash;

    public Block(String uuid, long timestamp, String previousHash, HashMap<String, String> tranxLst) {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.tranxLst = tranxLst;
        this.merkleRootHash = buildMerkleTree();
        this.hash = calculateHash();
    }

    // Getters
    public String getUUID() {
        return uuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public HashMap<String, String> getTranxLst() {
        return tranxLst;
    }

    public String getMerkleRootHash() {
        return merkleRootHash;
    }

    public String getHash() {
        return hash;
    }
    
    private String buildMerkleTree() {
        MerkleTree merkleTree = MerkleTree.getInstance(tranxLst);
        merkleTree.build();
        return merkleTree.getRoot();
    }

    private String calculateHash() {
        return Hasher.sha512(timestamp + previousHash + merkleRootHash);
    }
}