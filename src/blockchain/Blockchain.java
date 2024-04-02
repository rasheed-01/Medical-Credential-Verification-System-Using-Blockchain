package blockchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Blockchain {
    public List<Block> blocks;

    public Blockchain(boolean createGenesisBlock) {
        this.blocks = new ArrayList<>();
        if (createGenesisBlock) {
            createGenesisBlock();
        }
    }

    private void createGenesisBlock() {
        HashMap<String, String> data = new HashMap<>();
        data.put("data", "Genesis blockchain.Block");
        addBlock(new Block("0", System.currentTimeMillis(), "0", data));
    }

    public void addBlock(Block block) {
        blocks.add(block);
    }

    public Block getLatestBlock() {
        return blocks.get(blocks.size() - 1);
    }
}