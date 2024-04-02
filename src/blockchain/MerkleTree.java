package blockchain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MerkleTree {
    private HashMap<String, String> tranxLst;
    private String root;

    private MerkleTree(HashMap<String, String> tranxLst) {
        this.tranxLst = tranxLst;
        this.root = "";
    }

    private static MerkleTree instance;

    public static MerkleTree getInstance(HashMap<String, String> tranxLst) {
        if (instance == null) {
            instance = new MerkleTree(tranxLst);
        }
        return instance;
    }

    public String getRoot() {
        return root;
    }

    public void build() {
        List<String> tempLst = new ArrayList<>(tranxLst.values());
        List<String> hashes = generateTransactionHashList(tempLst);

        while (hashes.size() > 1) {
            hashes = generateTransactionHashList(hashes);
        }

        root = hashes.get(0);
    }

    private List<String> generateTransactionHashList(List<String> tranxLst) {
        List<String> hashLst = new ArrayList<>();

        for (int i = 0; i < tranxLst.size(); i += 2) {
            String left = tranxLst.get(i);
            String right = (i + 1 < tranxLst.size()) ? tranxLst.get(i + 1) : "";
            String hash = Hasher.sha512(left + right);
            hashLst.add(hash);
        }

        return hashLst;
    }
}
