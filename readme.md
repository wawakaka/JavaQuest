# "JavaQuest: The Algorithm Chronicles"

## Cara Menjalankan (How to Run)

### Requirement
- Java 17 or newer (JDK)

### With Gradle (Recommended)
```bash
./gradlew run
```

### Without Gradle
```bash
# Compile
javac -d out src/main/java/io/github/wawakaka/JavaQuest/*.java

# Run
java -cp out io.github.wawakaka.JavaQuest.JavaQuest
```

### Build
```bash
./gradlew build    # Build project
```

---

### 1. Deskripsi Umum (Game Overview)

**JavaQuest** adalah permainan petualangan berbasis teks (*text-based RPG*) di mana pemain berperan sebagai petualang yang menjelajahi dunia fiksi yang terdiri dari 16 lokasi yang saling terhubung melalui 21 jalur, dengan 33 item tersebar di seluruh peta.

Tujuan utama pemain adalah:

1. **Eksplorasi:** Menemukan lokasi-lokasi baru.
2. **Looting:** Mengumpulkan *item* berharga yang tersebar di peta.
3. **Survival:** Bertahan hidup menggunakan item penyembuh (*consumables*).
4. **Prestasi:** Mencapai skor tertinggi yang akan dicatat dalam *Leaderboard*.

---

### 2. Arsitektur Struktur Data (Technical Concept)

Ide inti dari proyek ini adalah **simulasi sistem dunia nyata** menggunakan tiga struktur data fundamental. Setiap struktur data menangani satu aspek logis dari permainan:

#### A. Peta Dunia = Graph (Undirected Graph)

Dunia permainan tidak linier (seperti garis lurus), melainkan bercabang.

* **Konsep:** Setiap lokasi adalah **Node** (Titik), dan setiap jalan penghubung adalah **Edge** (Garis).
* **Implementasi:** Menggunakan **Adjacency List** (`Map<String, List<String>>`).
* **Fungsi:** Memungkinkan pemain bergerak bebas (bolak-balik) antar lokasi dan memungkinkan implementasi fitur **Radar (BFS)** untuk mendeteksi lokasi di sekitar pemain.

#### B. Tas Penyimpanan = Hash Table

Pemain membutuhkan akses instan ke barang bawaannya tanpa peduli seberapa banyak barang yang ia bawa.

* **Konsep:** Penyimpanan berbasis pasangan **Kunci-Nilai** (*Key-Value*).
* **Implementasi:** `HashMap<String, Item>`.
* **Fungsi:**
* **Pencarian Cepat ():** Saat pemain mengetik `gunakan Potion`, program tidak perlu mencari satu per satu.
* **Stacking:** Jika pemain mengambil item yang sama berkali-kali, Hash Table hanya memperbarui jumlahnya (*quantity*), tidak membuat objek baru, sehingga hemat memori.



#### C. Papan Peringkat = Binary Search Tree (BST)

Skor pemain harus selalu terurut dari yang terkecil hingga terbesar (atau sebaliknya) setiap kali ada pemain baru yang selesai bermain.

* **Konsep:** Struktur pohon di mana anak kiri < induk < anak kanan.
* **Implementasi:** Node Pohon (`ScoreNode`) yang saling terhubung.
* **Fungsi:** Memungkinkan penyisipan skor baru (*insertion*) dan pencetakan daftar juara (*traversal*) secara efisien tanpa harus melakukan *sorting* ulang seluruh array secara manual.

---

### 3. Mekanisme Permainan (Gameplay Mechanics)

Game ini memiliki mekanisme yang realistis untuk ukuran *text-based game*:

1. **Realistis Item Drop (Sistem "Barang di Tanah"):**
* Item tidak muncul tiba-tiba dari udara. Item disimpan di dalam Node Graph (Lokasi).
* Jika pemain ada di "Hutan", dia hanya bisa mengambil item yang memang ada di list "Hutan".
* Setelah diambil (`ambil`), item hilang dari Node Graph dan pindah ke Hash Table (Tas Pemain).


2. **Manajemen Inventory:**
* **Consumables:** Item seperti "Potion" atau "Elixir" bisa habis dipakai. Jika jumlahnya 0, item otomatis dihapus dari Hash Table.
* **Permanents:** Item seperti "Pedang" atau "Peta" tidak akan hilang walau digunakan berkali-kali.


3. **Navigasi & Radar:**
* Pemain hanya bisa berpindah ke lokasi yang terhubung langsung.
* Pemain bisa menggunakan perintah `radar` yang memicu algoritma **Breadth-First Search (BFS)** untuk mengetahui apa saja lokasi yang ada dalam radius 2 langkah.

---

### Peta Dunia (World Map)

Dunia terdiri dari **16 lokasi** yang terhubung oleh **21 jalur**:

```
                    menara --- reruntuhan
                   /   |  \        \
        perkemahan   benteng  gunung -- tambang -- goa -- sarangnaga
           |           |                           |
pasar -- desa -- hutan -- rawa -- danau           (goa keeps existing connections)
  |        |               |       |
kuil     sungai          pantai--danau
  |        |
benteng  pantai
```

**Daftar lokasi:** Desa, Hutan, Goa, SarangNaga, Pasar, Sungai, Perkemahan, Rawa, Danau, Pantai, Kuil, Benteng, Gunung, Tambang, Menara, Reruntuhan

**Daftar jalur (21):**

| No | Jalur |
|----|-------|
| 1 | Desa -- Hutan |
| 2 | Hutan -- Goa |
| 3 | Goa -- SarangNaga |
| 4 | Desa -- Pasar |
| 5 | Desa -- Sungai |
| 6 | Hutan -- Rawa |
| 7 | Hutan -- Perkemahan |
| 8 | Goa -- Tambang |
| 9 | Sungai -- Pantai |
| 10 | Pasar -- Kuil |
| 11 | Pasar -- Benteng |
| 12 | Rawa -- Danau |
| 13 | Danau -- Pantai |
| 14 | Kuil -- Benteng |
| 15 | Perkemahan -- Gunung |
| 16 | Gunung -- Tambang |
| 17 | Gunung -- Menara |
| 18 | Menara -- Reruntuhan |
| 19 | Reruntuhan -- SarangNaga |
| 20 | Menara -- Benteng |
| 21 | Rawa -- Sungai |

### Daftar Item (33 Item)

| Lokasi | Item | Deskripsi | Power | Consumable |
|---|---|---|---|---|
| Desa | Peta | Peta dunia kuno | 0 | Tidak |
| Desa | Roti | Roti hangat dari rumah | 20 | Ya |
| Hutan | Potion | Penyembuh luka ringan | 50 | Ya |
| Hutan | Jamur | Jamur hutan beracun | 15 | Ya |
| Goa | Pedang | Pedang besi tua | 20 | Tidak |
| Goa | Elixir | Obat dewa | 100 | Ya |
| Goa | Potion | Penyembuh luka ringan | 50 | Ya |
| SarangNaga | Emas | Harta karun naga! | 500 | Tidak |
| SarangNaga | Mahkota | Mahkota sisik naga | 1000 | Tidak |
| Pasar | Perisai | Perisai kayu ringan | 15 | Tidak |
| Pasar | Panah | Anak panah tajam | 10 | Ya |
| Sungai | Tombak | Tombak bambu runcing | 18 | Tidak |
| Sungai | Ikan | Ikan bakar segar | 30 | Ya |
| Perkemahan | Obor | Obor menyala terang | 5 | Tidak |
| Perkemahan | Selimut | Selimut tebal penghangat | 10 | Tidak |
| Rawa | Racun | Racun kodok mematikan | 25 | Ya |
| Rawa | Permata | Permata hijau berkilau | 200 | Tidak |
| Danau | Mutiara | Mutiara putih bercahaya | 300 | Tidak |
| Danau | Tongkat | Tongkat kayu ajaib | 22 | Tidak |
| Pantai | Kerang | Kerang laut besar | 50 | Tidak |
| Pantai | Jaring | Jaring ikan kuat | 8 | Tidak |
| Kuil | Jimat | Jimat pelindung kuno | 30 | Tidak |
| Kuil | Mantra | Gulungan mantra suci | 60 | Ya |
| Benteng | Armor | Baju besi pejuang | 35 | Tidak |
| Benteng | Helm | Helm besi tua | 20 | Tidak |
| Gunung | Kapak | Kapak besi berat | 28 | Tidak |
| Gunung | Kristal | Kristal es abadi | 250 | Tidak |
| Tambang | Besi | Bongkahan besi mentah | 12 | Tidak |
| Tambang | Batu | Batu api berguna | 5 | Ya |
| Menara | Cincin | Cincin sihir misterius | 40 | Tidak |
| Menara | Gulungan | Peta langit kuno | 0 | Tidak |
| Reruntuhan | Liontin | Liontin raja purba | 150 | Tidak |
| Reruntuhan | Relik | Relik peradaban kuno | 400 | Tidak |

---

### 4. Alur Permainan (Flowchart Sederhana)

1. **Start:** Inisialisasi Peta (Graph), sebar Item di lokasi, dan muat Leaderboard awal (BST).
2. **Game Loop:**
* Program menampilkan Lokasi Saat Ini & Item di tanah.
* **Input User:**
* `jalan [arah]`: Cek Graph  Pindah Node.
* `radar`: Jalankan BFS.
* `ambil [item]`: Pindahkan objek dari Graph ke Hash Table.
* `gunakan [item]`: Cek Hash Table  Update status Player (HP/Score)  Update jumlah item.
* `keluar`: Keluar loop.
3. **Game Over:** Input nama pemain  Masukkan Skor ke BST  Tampilkan Leaderboard (In-Order Traversal).

---

## **Pseudocode**

---

### 1. Class `Item` (Model Data)

Class ini merepresentasikan objek barang.

```text
CLASS Item
    ATTRIBUTES:
        name : String
        description : String
        power : Integer
        quantity : Integer
        isConsumable : Boolean

    CONSTRUCTOR(name, desc, power, consumable):
        SET this.name = name
        SET this.description = desc
        SET this.power = power
        SET this.isConsumable = consumable
        SET this.quantity = 1

    FUNCTION addStack():
        INCREMENT quantity by 1

    FUNCTION removeStack():
        DECREMENT quantity by 1
END CLASS

```

---

### 2. Class `Inventory` (Implementasi Hash Table)

Mengelola penyimpanan barang milik pemain.

```text
CLASS Inventory
    ATTRIBUTES:
        items : HashTable<String, Item>  // Key: Nama Item, Value: Objek Item
        maxSlots : Integer = 10

    FUNCTION addItem(newItem):
        key = LOWERCASE(newItem.name)

        IF items CONTAINS key THEN
            // Logika Stacking (Efisiensi Hash Table)
            EXISTING_ITEM = items.GET(key)
            CALL EXISTING_ITEM.addStack()
            PRINT "Item ditumpuk."
        ELSE
            IF items.SIZE >= maxSlots THEN
                PRINT "Tas penuh!"
                RETURN
            END IF
            items.PUT(key, newItem)
            PRINT "Item baru ditambahkan."
        END IF

    FUNCTION useItem(itemName):
        key = LOWERCASE(itemName)

        IF items CONTAINS key THEN
            item = items.GET(key)
            RETURN item  // Kembalikan objek item untuk diproses efeknya
        ELSE
            PRINT "Item tidak ditemukan."
            RETURN NULL
        END IF

    FUNCTION removeItemIfEmpty(itemName):
        key = LOWERCASE(itemName)
        item = items.GET(key)
        
        IF item.isConsumable AND item.quantity <= 0 THEN
            items.REMOVE(key)  // Hapus dari Hash Table
            PRINT "Item habis."
        END IF
END CLASS

```

---

### 3. Class `WorldMap` (Implementasi Graph)

Mengelola peta, koneksi antar lokasi, dan item yang tercecer di tanah.

```text
CLASS WorldMap
    ATTRIBUTES:
        adjList : HashMap<String, List<String>>  // Graph (Adjacency List)
        roomItems : HashMap<String, List<Item>>  // Item di setiap lokasi

    FUNCTION addPath(locationA, locationB):
        // Undirected Graph (Dua Arah)
        adjList[locationA].ADD(locationB)
        adjList[locationB].ADD(locationA)

    FUNCTION dropItem(location, item):
        roomItems[location].ADD(item)

    FUNCTION pickUpItem(location, itemName):
        listItems = roomItems[location]
        
        FOR EACH item IN listItems:
            IF item.name EQUALS itemName THEN
                listItems.REMOVE(item)  // Hapus dari tanah (Graph Node)
                RETURN item             // Serahkan ke pemain
            END IF
        END FOR
        RETURN NULL

    FUNCTION bfsRadar(startLocation, range):
        // Algoritma Breadth-First Search
        CREATE Queue queue
        CREATE Set visited
        CREATE Map distances

        queue.ENQUEUE(startLocation)
        visited.ADD(startLocation)
        distances.PUT(startLocation, 0)

        WHILE queue IS NOT EMPTY:
            current = queue.DEQUEUE()
            currentDist = distances.GET(current)

            IF currentDist > range THEN BREAK LOOP

            PRINT current + " (" + currentDist + " langkah)"

            FOR EACH neighbor IN adjList[current]:
                IF neighbor NOT IN visited:
                    visited.ADD(neighbor)
                    distances.PUT(neighbor, currentDist + 1)
                    queue.ENQUEUE(neighbor)
                END IF
            END FOR
        END WHILE
END CLASS

```

---

### 4. Class `ScoreBST` (Implementasi Binary Search Tree)

Mengelola papan peringkat (Leaderboard).

```text
CLASS ScoreNode
    ATTRIBUTES:
        playerName : String
        score : Integer
        left : ScoreNode
        right : ScoreNode
END CLASS

CLASS ScoreBST
    ATTRIBUTES:
        root : ScoreNode

    FUNCTION insert(name, score):
        root = RECURSIVE_INSERT(root, name, score)

    FUNCTION RECURSIVE_INSERT(node, name, score):
        IF node IS NULL THEN
            RETURN NEW ScoreNode(name, score)
        END IF

        IF score < node.score THEN
            node.left = RECURSIVE_INSERT(node.left, name, score)
        ELSE
            node.right = RECURSIVE_INSERT(node.right, name, score)
        END IF
        RETURN node

    FUNCTION showLeaderboard():
        // Reverse In-Order Traversal (Kanan -> Akar -> Kiri)
        // Agar skor terbesar muncul paling atas
        CALL recursiveShow(root)

    FUNCTION recursiveShow(node):
        IF node IS NOT NULL THEN
            recursiveShow(node.right)
            PRINT node.playerName + ": " + node.score
            recursiveShow(node.left)
        END IF
END CLASS

```

---

### 5. Class `JavaQuest` (Main Game Logic)

Menggabungkan semua struktur data menjadi alur permainan.

```text
CLASS JavaQuest
    GLOBAL VARIABLES:
        world : WorldMap
        inventory : Inventory
        leaderboard : ScoreBST
        playerLocation : String = "StartNode"
        playerHP : Integer = 100
        gameRunning : Boolean = TRUE

    FUNCTION MAIN():
        CALL setupGameWorld()  // Isi Graph dan drop items

        WHILE gameRunning IS TRUE:
            PRINT "Lokasi: " + playerLocation
            CALL world.lookAround(playerLocation) // Tampilkan item di tanah
            INPUT userCommand

            SWITCH userCommand:
                CASE "JALAN [destination]":
                    IF world.canGo(playerLocation, destination) THEN
                        playerLocation = destination
                    ELSE
                        PRINT "Jalan buntu."
                    END IF

                CASE "AMBIL [itemName]":
                    foundItem = world.pickUpItem(playerLocation, itemName)
                    IF foundItem IS NOT NULL THEN
                        inventory.addItem(foundItem)
                    ELSE
                        PRINT "Barang tidak ada."
                    END IF

                CASE "GUNAKAN [itemName]":
                    item = inventory.useItem(itemName)
                    IF item IS NOT NULL THEN
                        // Efek Item
                        IF item.name == "Potion" THEN playerHP += 50

                        IF item.isConsumable THEN
                            item.removeStack()
                            inventory.removeItemIfEmpty(itemName)
                        END IF
                    END IF

                CASE "RADAR":
                    CALL world.bfsRadar(playerLocation, 2)

                CASE "KELUAR":
                    gameRunning = FALSE
                    CALL handleGameOver()

            END SWITCH
        END WHILE

    FUNCTION setupGameWorld():
        // 21 jalur (Undirected Graph)
        world.addPath("desa", "hutan")
        world.addPath("hutan", "goa")
        world.addPath("goa", "sarangnaga")
        world.addPath("desa", "pasar")
        world.addPath("desa", "sungai")
        world.addPath("hutan", "rawa")
        world.addPath("hutan", "perkemahan")
        world.addPath("goa", "tambang")
        world.addPath("sungai", "pantai")
        world.addPath("pasar", "kuil")
        world.addPath("pasar", "benteng")
        world.addPath("rawa", "danau")
        world.addPath("danau", "pantai")
        world.addPath("kuil", "benteng")
        world.addPath("perkemahan", "gunung")
        world.addPath("gunung", "tambang")
        world.addPath("gunung", "menara")
        world.addPath("menara", "reruntuhan")
        world.addPath("reruntuhan", "sarangnaga")
        world.addPath("menara", "benteng")
        world.addPath("rawa", "sungai")

        // 33 item tersebar di 16 lokasi
        world.dropItem("desa", Item("Peta", "Peta dunia kuno", 0, false))
        world.dropItem("desa", Item("Roti", "Roti hangat dari rumah", 20, true))
        world.dropItem("hutan", Item("Potion", "Penyembuh luka ringan", 50, true))
        world.dropItem("hutan", Item("Jamur", "Jamur hutan beracun", 15, true))
        world.dropItem("goa", Item("Pedang", "Pedang besi tua", 20, false))
        world.dropItem("goa", Item("Elixir", "Obat dewa", 100, true))
        world.dropItem("goa", Item("Potion", "Penyembuh luka ringan", 50, true))
        world.dropItem("sarangnaga", Item("Emas", "Harta karun naga!", 500, false))
        world.dropItem("sarangnaga", Item("Mahkota", "Mahkota sisik naga", 1000, false))
        // ... dan 24 item lainnya di lokasi pasar, sungai, perkemahan,
        //     rawa, danau, pantai, kuil, benteng, gunung, tambang,
        //     menara, reruntuhan (lihat tabel item di atas)

    FUNCTION handleGameOver():
        INPUT playerName
        leaderboard.insert(playerName, currentScore)
        leaderboard.showLeaderboard()
END CLASS
```