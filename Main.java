import java.util.*;

class Node {
    String v;
    List<Node> links;
    Node parent;
    int anc_locked, dec_locked, uid;
    boolean isLocked;

    Node(String x, Node p) {
        v = x;
        parent = p;
        anc_locked = 0;
        dec_locked = 0;
        uid = 0;
        isLocked = false;
        links = new ArrayList<>();
    }

    void addLinks(List<String> l, Node p) {
        for (String i : l)
            links.add(new Node(i, p));
    }
}

class Tree {
    private Node root;
    private Map<String, Node> vton;

    Tree(Node r) {
        root = r;
        vton = new HashMap<>();
    }

    Node getRoot() {
        return root;
    }

    void fillVtoN(Node r) {
        if (r == null)
            return;
        vton.put(r.v, r);
        for (Node k : r.links)
            fillVtoN(k);
    }

    void informDescendants(Node r, int val) {
        for (Node k : r.links) {
            k.anc_locked += val;
            informDescendants(k, val);
        }
    }

    boolean verifyDescendants(Node r, int id, List<Node> v) {
        if (r.isLocked) {
            if (r.uid != id)
                return false;
            v.add(r);
        }
        if (r.dec_locked == 0)
            return true;

        boolean ans = true;
        for (Node k : r.links) {
            ans &= verifyDescendants(k, id, v);
            if (!ans)
                return false;
        }
        return ans;
    }

    boolean lock(String v, int id) {
        Node t = vton.get(v);
        if (t.isLocked)
            return false;

        if (t.anc_locked != 0)
            return false;
        if (t.dec_locked != 0)
            return false;

        Node cur = t.parent;
        while (cur != null) {
            cur.dec_locked++;
            cur = cur.parent;
        }
        informDescendants(t, 1);
        t.isLocked = true;
        t.uid = id;
        return true;
    }

    boolean unlock(String v, int id) {
        Node t = vton.get(v);
        if (!t.isLocked)
            return false;
        if (t.isLocked && t.uid != id)
            return false;

        Node cur = t.parent;
        while (cur != null) {
            cur.dec_locked--;
            cur = cur.parent;
        }
        informDescendants(t, -1);
        t.isLocked = false;
        return true;
    }

    boolean upgrade(String v, int id) {
        Node t = vton.get(v);
        if (t.isLocked)
            return false;

        if (t.anc_locked != 0)
            return false;
        if (t.dec_locked == 0)
            return false;

        List<Node> vec = new ArrayList<>();
        if (verifyDescendants(t, id, vec)) {
            for (Node k : vec) {
                unlock(k.v, id);
            }
        } else
            return false;
        lock(v, id);
        return true;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int q = scanner.nextInt();

        List<String> s = new ArrayList<>();
        for (int i = 0; i < n; i++)
            s.add(scanner.next());

        Node r = new Node(s.get(0), null);
        r = buildTree(r, m, s);

        Tree t = new Tree(r);
        t.fillVtoN(t.getRoot());

        int op, uid;
        String sq;
        for (int i = 0; i < q; i++) {
            op = scanner.nextInt();
            sq = scanner.next();
            uid = scanner.nextInt();
            switch (op) {
                case 1:
                    if (t.lock(sq, uid))
                        System.out.println("true");
                    else
                        System.out.println("false");
                    break;
                case 2:
                    if (t.unlock(sq, uid))
                        System.out.println("true");
                    else
                        System.out.println("false");
                    break;
                case 3:
                    if (t.upgrade(sq, uid))
                        System.out.println("true");
                    else
                        System.out.println("false");
                    break;
            }
        }
        scanner.close();
    }

    private static Node buildTree(Node root, int m, List<String> s) {
        Queue<Node> q = new LinkedList<>();
        q.add(root);

        int st = 1;
        while (!q.isEmpty()) {
            Node r = q.poll();

            if (st >= s.size())
                continue;

            List<String> temp = new ArrayList<>();
            for (int i = st; i < st + m; i++)
                temp.add(s.get(i));
            r.addLinks(temp, r);
            st += m;

            for (Node k : r.links)
                q.add(k);
        }

        return root;
    }
}
