package io.github.wawakaka.JavaQuest;

import java.util.*;

class Item {
    String name;
    String description;
    int power;          // Bisa berarti Damage (Senjata) atau Heal (Potion)
    int quantity;
    boolean isConsumable; // True = hilang setelah dipakai, False = tetap ada

    public Item(String name, String desc, int power, boolean isConsumable) {
        this.name = name;
        this.description = desc;
        this.power = power;
        this.isConsumable = isConsumable;
        this.quantity = 1;
    }

    public void addStack() {
        this.quantity++;
    }

    public void removeStack() {
        this.quantity--;
    }

    @Override
    public String toString() {
        // Tampilan: "Potion (x3) - Pemulih Darah"
        return String.format("%-15s (x%d) | %s", name, quantity, description);
    }
}

public class Inventory {
    private Map<String, Item> items;
    private final int MAX_SLOTS = 10;

    public Inventory() {
        this.items = new HashMap<>();
    }

    public void addItem(String name, String desc, int power, boolean isConsumable) {
        String key = name.toLowerCase();

        // Cek Hash Table: Apakah item ini sudah ada? O(1)
        if (items.containsKey(key)) {
            // Jika ada, jangan buat objek baru. Cukup tambah jumlahnya.
            Item existingItem = items.get(key);
            existingItem.addStack();
            System.out.println("[+] " + name + " ditumpuk! Sekarang ada " + existingItem.quantity);
        } else {
            if (items.size() >= MAX_SLOTS) {
                System.out.println("[X] Tas penuh! Tidak bisa mengambil " + name);
                return;
            }
            items.put(key, new Item(name, desc, power, isConsumable));
            System.out.println("[+] " + name + " dimasukkan ke tas.");
        }
    }

    public boolean useItem(String name) {
        String key = name.toLowerCase();

        if (items.containsKey(key)) {
            Item item = items.get(key);

            System.out.println("[*] Menggunakan " + item.name + "...");
            System.out.println("   Efek: " + item.power + " poin.");

            if (item.isConsumable) {
                item.removeStack();
                removeItemIfEmpty(name);
                if (item.quantity > 0) {
                    System.out.println("   Sisa " + item.name + ": " + item.quantity);
                }
            } else {
                System.out.println("   (Item ini tidak habis dipakai)");
            }
            return true;
        } else {
            System.out.println("[?] Kamu tidak punya item: " + name);
            return false;
        }
    }

    public void removeItemIfEmpty(String itemName) {
        String key = itemName.toLowerCase();
        Item item = items.get(key);
        if (item != null && item.isConsumable && item.quantity <= 0) {
            items.remove(key);
            System.out.println("   " + item.name + " telah habis.");
        }
    }

    public void showInventory() {
        System.out.println("\n=== ISI TAS (" + items.size() + "/" + MAX_SLOTS + ") ===");
        if (items.isEmpty()) {
            System.out.println("(Kosong mlompong)");
        } else {
            for (Item item : items.values()) {
                System.out.println("- " + item.toString());
            }
        }
        System.out.println("============================");
    }
}
