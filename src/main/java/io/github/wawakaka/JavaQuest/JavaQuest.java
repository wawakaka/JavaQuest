package io.github.wawakaka.JavaQuest;

import java.util.Scanner;

public class JavaQuest {
    static WorldMap world = new WorldMap();
    static Inventory inventory = new Inventory();
    static ScoreBST leaderboard = new ScoreBST();
    static Scanner scanner = new Scanner(System.in);

    static String currentLocation = "desa";
    static int currentHP = 100;
    static int maxHP = 100;
    static int score = 0;
    static boolean isRunning = true;

    public static void main(String[] args) {
        setupGameWorld();

        System.out.println("========================================");
        System.out.println("  JAVAQUEST: THE REALISTIC CHRONICLES   ");
        System.out.println("========================================");

        world.lookAround(currentLocation);

        while (isRunning) {
            System.out.println("\n[Perintah] [argument]");
            System.out.println("Perintah: jalan | lihat | ambil | tas | gunakan (menggunakan item yang ada di tas) | radar (melihat lokasi yang bisa dituju) | keluar (keluar dari game)");
            System.out.print("Lokasi saat ini: [" + currentLocation + "]\n");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) continue;

            String[] parts = input.split(" ", 2);
            String command = parts[0].toLowerCase();
            String argument = parts.length > 1 ? parts[1] : "";

            switch (command) {
                case "jalan":
                    handleMove(argument);
                    break;
                case "lihat":
                    world.lookAround(currentLocation);
                    break;
                case "tas":
                    inventory.showInventory();
                    break;
                case "ambil":
                    handleTake(argument);
                    break;
                case "gunakan":
                    handleUse(argument);
                    break;
                case "radar":
                    world.bfsRadar(currentLocation, 2);
                    break;
                case "keluar":
                    handleGameOver();
                    break;
                default:
                    System.out.println("Perintah salah. Coba: jalan, lihat, ambil, tas, gunakan, keluar");
            }
        }
    }

    static void handleMove(String dest) {
        // Kita perbaiki logika match string biar user bisa ketik "hutan" (kecil) walau data "Hutan" (besar)
        String actualDest = null;
        String exits = world.getExits(currentLocation);

        if (world.canGo(currentLocation, dest)) {
            // Untuk simpel, kita anggap user mengetik nama yang benar atau kita 'capitalize'
            currentLocation = dest.toLowerCase();
            System.out.println("Berjalan ke " + currentLocation + "...");
            world.lookAround(currentLocation);
        } else {
            System.out.println("[X] Tidak ada jalan ke sana!");
        }
    }

    // Helper kecil buat merapikan input user "hutan" jadi "Hutan"
    static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    static void handleTake(String itemName) {
        if (itemName.isEmpty()) {
            System.out.println("Ambil apa?");
            return;
        }

        // 1. Cek ke WorldMap: Ada gak barangnya di lokasi ini?
        Item foundItem = world.pickUpItem(currentLocation, itemName);

        if (foundItem != null) {
            inventory.addItem(foundItem.name, foundItem.description, foundItem.power, foundItem.isConsumable);

            System.out.println("[+] Kamu mengambil [" + foundItem.name + "]!");
            score += 10;
        } else {
            System.out.println("Tidak ada benda bernama '" + itemName + "' di sini.");
        }
    }

    static void handleUse(String itemName) {
        if (inventory.useItem(itemName)) { // Asumsi useItem return boolean
            if (itemName.equalsIgnoreCase("Potion")) healPlayer(50);
            if (itemName.equalsIgnoreCase("Elixir")) healPlayer(100);
        }
    }

    static void healPlayer(int amount) {
        currentHP += amount;
        if (currentHP > maxHP) currentHP = maxHP;
        System.out.println("[*] HP pulih! (" + currentHP + "/" + maxHP + ")");
    }

    static void handleGameOver() {
        System.out.println("Game Over. Skor: " + score);
        System.out.print("Nama: ");
        String name = scanner.nextLine();
        leaderboard.insert(name, score);
        leaderboard.showLeaderboard();
        isRunning = false;
    }

    static void setupGameWorld() {
        world.addPath("desa", "hutan");
        world.addPath("hutan", "goa");
        world.addPath("goa", "sarangnaga");
        world.addPath("desa", "pasar");
        world.addPath("desa", "sungai");
        world.addPath("hutan", "rawa");
        world.addPath("hutan", "perkemahan");
        world.addPath("goa", "tambang");
        world.addPath("sungai", "pantai");
        world.addPath("pasar", "kuil");
        world.addPath("pasar", "benteng");
        world.addPath("rawa", "danau");
        world.addPath("danau", "pantai");
        world.addPath("kuil", "benteng");
        world.addPath("perkemahan", "gunung");
        world.addPath("gunung", "tambang");
        world.addPath("gunung", "menara");
        world.addPath("menara", "reruntuhan");
        world.addPath("reruntuhan", "sarangnaga");
        world.addPath("menara", "benteng");
        world.addPath("rawa", "sungai");

        world.dropItem("desa", new Item("Peta", "Peta dunia kuno", 0, false));
        world.dropItem("hutan", new Item("Potion", "Penyembuh luka ringan", 50, true));
        world.dropItem("goa", new Item("Pedang", "Pedang besi tua", 20, false));
        world.dropItem("goa", new Item("Elixir", "Obat dewa", 100, true));
        world.dropItem("sarangnaga", new Item("Emas", "Harta karun naga!", 500, false));
        world.dropItem("desa", new Item("Roti", "Roti hangat dari rumah", 20, true));
        world.dropItem("pasar", new Item("Perisai", "Perisai kayu ringan", 15, false));
        world.dropItem("pasar", new Item("Panah", "Anak panah tajam", 10, true));
        world.dropItem("sungai", new Item("Tombak", "Tombak bambu runcing", 18, false));
        world.dropItem("sungai", new Item("Ikan", "Ikan bakar segar", 30, true));
        world.dropItem("perkemahan", new Item("Obor", "Obor menyala terang", 5, false));
        world.dropItem("perkemahan", new Item("Selimut", "Selimut tebal penghangat", 10, false));
        world.dropItem("rawa", new Item("Racun", "Racun kodok mematikan", 25, true));
        world.dropItem("rawa", new Item("Permata", "Permata hijau berkilau", 200, false));
        world.dropItem("danau", new Item("Mutiara", "Mutiara putih bercahaya", 300, false));
        world.dropItem("danau", new Item("Tongkat", "Tongkat kayu ajaib", 22, false));
        world.dropItem("pantai", new Item("Kerang", "Kerang laut besar", 50, false));
        world.dropItem("pantai", new Item("Jaring", "Jaring ikan kuat", 8, false));
        world.dropItem("kuil", new Item("Jimat", "Jimat pelindung kuno", 30, false));
        world.dropItem("kuil", new Item("Mantra", "Gulungan mantra suci", 60, true));
        world.dropItem("benteng", new Item("Armor", "Baju besi pejuang", 35, false));
        world.dropItem("benteng", new Item("Helm", "Helm besi tua", 20, false));
        world.dropItem("gunung", new Item("Kapak", "Kapak besi berat", 28, false));
        world.dropItem("gunung", new Item("Kristal", "Kristal es abadi", 250, false));
        world.dropItem("tambang", new Item("Besi", "Bongkahan besi mentah", 12, false));
        world.dropItem("tambang", new Item("Batu", "Batu api berguna", 5, true));
        world.dropItem("menara", new Item("Cincin", "Cincin sihir misterius", 40, false));
        world.dropItem("menara", new Item("Gulungan", "Peta langit kuno", 0, false));
        world.dropItem("reruntuhan", new Item("Liontin", "Liontin raja purba", 150, false));
        world.dropItem("reruntuhan", new Item("Relik", "Relik peradaban kuno", 400, false));
        world.dropItem("sarangnaga", new Item("Mahkota", "Mahkota sisik naga", 1000, false));
        world.dropItem("goa", new Item("Potion", "Penyembuh luka ringan", 50, true));
        world.dropItem("hutan", new Item("Jamur", "Jamur hutan beracun", 15, true));
    }
}
