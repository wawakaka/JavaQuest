package io.github.wawakaka.JavaQuest;

import java.util.*;

public class WorldMap {
    // Graph Koneksi Antar Lokasi
    private Map<String, List<String>> adjList;

    // BARU: Inventaris Ruangan (Item apa saja yang ada di tanah?)
    private Map<String, List<Item>> roomItems;

    public WorldMap() {
        this.adjList = new HashMap<>();
        this.roomItems = new HashMap<>();
    }

    public void addLocation(String location) {
        location = location.toLowerCase();
        adjList.putIfAbsent(location, new ArrayList<>());
        roomItems.putIfAbsent(location, new ArrayList<>());
    }

    public void addPath(String loc1, String loc2) {
        loc1 = loc1.toLowerCase();
        loc2 = loc2.toLowerCase();
        addLocation(loc1);
        addLocation(loc2);
        adjList.get(loc1).add(loc2);
        adjList.get(loc2).add(loc1);
    }

    // Menaruh item di lokasi tertentu (Saat setup game)
    public void dropItem(String location, Item item) {
        location = location.toLowerCase();
        addLocation(location);
        roomItems.get(location).add(item);
    }

    // Mengambil item dari lokasi (Pemain melakukan 'ambil')
    public Item pickUpItem(String location, String itemName) {
        location = location.toLowerCase();
        List<Item> itemsOnGround = roomItems.get(location);

        if (itemsOnGround != null) {
            for (int i = 0; i < itemsOnGround.size(); i++) {
                Item item = itemsOnGround.get(i);
                // Cek nama item (ignore case)
                if (item.name.equalsIgnoreCase(itemName)) {
                    itemsOnGround.remove(i);
                    return item;
                }
            }
        }
        return null;
    }

    // Melihat item apa saja yang ada di lokasi ini
    public void lookAround(String location) {
        location = location.toLowerCase();
        List<Item> items = roomItems.get(location);
        System.out.println("Jalan tersedia: " + getExits(location));

        if (items != null && !items.isEmpty()) {
            System.out.println("Kamu melihat sesuatu di tanah:");
            for (Item item : items) {
                System.out.println("   - [" + item.name + "] " + item.description);
            }
        } else {
            System.out.println("   (Tidak ada barang menarik di sini)");
        }
    }

    public boolean canGo(String current, String destination) {
        current = current.toLowerCase();
        destination = destination.toLowerCase();
        if (!adjList.containsKey(current)) return false;
        for(String neighbor : adjList.get(current)) {
            if(neighbor.equals(destination)) return true;
        }
        return false;
    }

    public String getExits(String current) {
        current = current.toLowerCase();
        if (!adjList.containsKey(current)) return "Tidak ada.";
        return adjList.get(current).toString();
    }

    public void bfsRadar(String startLocation, int range) {
        startLocation = startLocation.toLowerCase();
        if (!adjList.containsKey(startLocation)) return;
        System.out.println("\n--- RADAR ---");
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, Integer> distance = new HashMap<>();

        queue.add(startLocation);
        visited.add(startLocation);
        distance.put(startLocation, 0);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDist = distance.get(current);
            if (currentDist > range) break;
            if (currentDist > 0) System.out.println("- " + current + " (" + currentDist + " langkah)");

            for (String neighbor : adjList.get(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    distance.put(neighbor, currentDist + 1);
                    queue.add(neighbor);
                }
            }
        }
    }
}
