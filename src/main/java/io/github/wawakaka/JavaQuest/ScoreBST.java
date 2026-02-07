package io.github.wawakaka.JavaQuest;

// Class Node: Representasi satu data pemain di dalam Tree
class ScoreNode {
    String playerName;
    int score;
    ScoreNode left;  // Anak kiri (Skor lebih kecil)
    ScoreNode right; // Anak kanan (Skor lebih besar)

    public ScoreNode(String name, int score) {
        this.playerName = name;
        this.score = score;
        this.left = null;
        this.right = null;
    }
}

// Class BST: Pengelola Logika Tree
public class ScoreBST {
    private ScoreNode root;

    public ScoreBST() {
        this.root = null;
    }

    // Dipanggil saat game over untuk menyimpan skor baru
    public void insert(String name, int score) {
        root = insertRec(root, name, score);
    }

    private ScoreNode insertRec(ScoreNode current, String name, int score) {
        // Base Case: Jika posisi kosong, buat node baru di sini
        if (current == null) {
            return new ScoreNode(name, score);
        }

        // Jika skor baru < skor sekarang -> Masuk Kiri
        // Jika skor baru >= skor sekarang -> Masuk Kanan
        if (score < current.score) {
            current.left = insertRec(current.left, name, score);
        } else if (score >= current.score) {
            current.right = insertRec(current.right, name, score);
        }

        return current;
    }

    // Dipanggil untuk menampilkan Leaderboard dari Tertinggi ke Terendah
    public void showLeaderboard() {
        System.out.println("\n=== HALL OF FAME ===");
        System.out.println("---------------------------");
        System.out.println("Rank | Player    | Score");
        System.out.println("---------------------------");

        // Kita pakai array int [1] sebagai trik untuk pass-by-reference counter rank
        reverseInorder(root, new int[]{1});

        System.out.println("---------------------------");
    }

    // Kenapa Reverse? Karena kita mau Right (Besar) -> Root -> Left (Kecil)
    private void reverseInorder(ScoreNode node, int[] rank) {
        if (node != null) {
            // 1. Kunjungi anak Kanan dulu (Skor Besar)
            reverseInorder(node.right, rank);

            System.out.printf("#%-3d | %-9s | %d\n", rank[0]++, node.playerName, node.score);

            // 3. Kunjungi anak Kiri (Skor Kecil)
            reverseInorder(node.left, rank);
        }
    }

    // Untuk mengecek apakah skor tertentu pernah dicapai
    public boolean searchScore(int score) {
        return searchRec(root, score);
    }

    private boolean searchRec(ScoreNode node, int score) {
        if (node == null) return false;
        if (node.score == score) return true;

        // Logic pencarian: Lebih kecil ke kiri, lebih besar ke kanan
        if (score < node.score) return searchRec(node.left, score);
        else return searchRec(node.right, score);
    }
}
