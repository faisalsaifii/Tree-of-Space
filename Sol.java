import java.util.*;
public class Sol {
    static class Node {
        String name;
        boolean isLocked;
        int id;
        Node parent;
        int anc_locked;
        int des_locked;
        ArrayList<Node> links = new ArrayList<>();

        Node (String name, Node parent) {
            this.name = name;
            this.parent = parent;
            anc_locked = 0;
            des_locked = 0;
            id = 0;
            isLocked = false;
        }

        void addLinks (List<String> childrenNames, Node parent) {
            for (String i: childrenNames)
                this.links.add(new Node(i, parent));
        }
    }

    static class Tree {
        private Node root;
        private Map<String,Node> map;

        public Tree (Node root) {
            this.root = root;
        }

        public Node getRoot () {
            return this.root;
        }

        public void fillMap (Node root) {
            if (root == null) return;
            map.put(root.name, root);
            for(Node i: root.links) fillMap(i);
        }

        public static void informDescendants (Node node, int val) {
            for (Node i: node.links) {
                i.anc_locked += val;
                informDescendants(i, val);
            } 
        }
        public static void desChange (Node node, int val) {
            while (node != null) {
                node.des_locked+=val;
                node = node.parent;
            }
        }
        public static boolean lock (Node node, int id) {
            if (node.isLocked || node.anc_locked > 0 || node.des_locked > 0) return false;
            desChange(node, 1);
            ancChange(node, 1);
            node.isLocked = true;
            node.id = id;
            return true;
        }
        public static boolean unlock (Node node, int id) {
            if (!node.isLocked || node.id != id) return false;
            desChange(node, -1);
            ancChange(node, -1);
            node.isLocked = false;
            node.id = 0;
            return true;
        }
        public static boolean getChildren (Node node, List<Node> arr, int id) {
            if (node == null) return false;
            if (node.isLocked) {
                if (node.id != id) return false;
                else arr.add(node);
            }
            if (node.des_locked == 0) return true;
            for(Node i: node.children) {
                boolean ans = getChildren(i, arr, id);
                if (!ans) return false;
            }
            return true;
        }
        public static boolean upgrade (Node node, int id) {
            if (node.isLocked || node.anc_locked > 0 || node.des_locked == 0) return false;
            List<Node> arr = new ArrayList<>();
            boolean can = getChildren(node, arr, id);
            if (!can) return false;
            for(Node i: arr) unlock(i, id);
            node.isLocked = true;
            node.id = id;
            return true;
        }        
    }

    static Node buildTree (Node root, int m, int n, String[] names) {
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        int index = 1;
        while (!queue.isEmpty() || index < n) {
            int size = queue.size();
            while (size-->0) {
                Node node = queue.poll();
                for(int i=0;i<=m && index < n; i++) {
                    Node newNode = new Node(names[index],node);
                    node.links.add(newNode);
                    queue.add(newNode);
                    index++;
                }
            }
        }
        return root;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int m = sc.nextInt();
        int q = sc.nextInt();
        sc.nextLine();

        String[] nodeStrings = new String[n];
        for(int i=0;i<n;i++) nodeStrings[i] = sc.nextLine();

        Node root = new Node(nodeStrings[0],null);
        root = buildTree(root, m, n, nodeStrings);

        Tree t = new Tree(root);
        t.fillMap(t.getRoot());
        
        for(int i=0;i<q;i++) {
            int op = sc.nextInt();
            String str = sc.next();
            int id = sc.nextInt();
            switch(op) {
                case 1:
                    if (t.lock(str, id)) System.out.println("true");
                    else System.out.println("false");
                    break;
                case 2:
                    if (t.unlock(str, id)) System.out.println("true");
                    else System.out.println("false");
                    break;
                case 3:
                    if (t.upgrade(str, id)) System.out.println("true");
                    else System.out.println("false");
                    break;
            }
        }
        sc.close();
    }
}