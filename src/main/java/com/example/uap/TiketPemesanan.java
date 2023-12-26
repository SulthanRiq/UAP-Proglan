package com.example.uap;

/**
 * Aplikasi untuk pemesanan tiket kereta
 */

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.util.Objects;

public class TiketPemesanan extends Application {

    TableView<Order> tableView = new TableView<>();
    private static final String DATA_FILE_PATH = "tiket_data.txt";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Pemesanan Tiket Kereta");
        primaryStage.setHeight(700);
        primaryStage.setWidth(900);

        Text text = new Text("Pemesanan Tiket Kereta");
        text.setFont(Font.font("Arial", FontWeight.BOLD, 20));


        tableView.setEditable(true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        // Inisialisasi antarmuka pengguna
        VBox vbox = new VBox();
        vbox.setSpacing(8);
        vbox.setPadding(new Insets(20, 10, 10, 10));
        vbox.setAlignment(Pos.TOP_CENTER);

        Label labelNama = new Label("Nama:");
        TextField addNama = new TextField();
        addNama.setPromptText("Nama");
        addNama.setAlignment(Pos.TOP_CENTER);

        Label labelJumlahTiket = new Label("Jumlah Tiket:");
        TextField addJumlah = new TextField();
        addJumlah.setPromptText("Jumlah Tiket");
        addJumlah.setAlignment(Pos.TOP_CENTER);

        Label labelJam = new Label("Jam:");
        TextField addJam = new TextField();
        addJam.setPromptText("Jam");
        addJam.setAlignment(Pos.TOP_CENTER);

        Label labelLokasi = new Label("Lokasi:");
        TextField addLokasi = new TextField();
        addLokasi.setPromptText("Lokasi");
        addLokasi.setAlignment(Pos.TOP_CENTER);

        Label labelNomorTiket = new Label("Nomor Tiket:");
        TextField addNomor = new TextField();
        addNomor.setPromptText("Nomor Tiket");
        addNomor.setAlignment(Pos.TOP_CENTER);

        Button buttonPesan = new Button("Pesan Tiket");

        buttonPesan.setOnAction(e -> {
            // Logika pesan tiket
            String nama = addNama.getText();
            String jumlahTiketStr = addJumlah.getText();
            String jam = addJam.getText();
            String lokasi = addLokasi.getText();
            String nomorTiket = addNomor.getText();

            try {
                // Validasi input
                validateInput(nama, jumlahTiketStr, jam, lokasi, nomorTiket);

                int jumlahTiket = Integer.parseInt(jumlahTiketStr);

                Pemberitahuan("Data berhasil ditambahkan!");

                // Simpan data pesanan tiket ke file
                saveOrderData(nama, jumlahTiket, jam, lokasi, nomorTiket);

                addNama.clear();
                addJumlah.clear();
                addJam.clear();
                addLokasi.clear();
                addNomor.clear();

                refreshTable();
            } catch (IllegalArgumentException ex) {
                Peringatan("Isi terlebih dahulu!");
            }
        });

        // Tabel untuk menampilkan data dari file

        TableColumn<Order, String> columnNama = new TableColumn<>("Nama");
        TableColumn<Order, Integer> columnJumlahTiket = new TableColumn<>("Jumlah Tiket");
        TableColumn<Order, String> columnJam = new TableColumn<>("Jam");
        TableColumn<Order, String> columnLokasi = new TableColumn<>("Lokasi");
        TableColumn<Order, String> columnNomorTiket = new TableColumn<>("Nomor Tiket");

        columnNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        columnJumlahTiket.setCellValueFactory(new PropertyValueFactory<>("jumlahTiket"));
        columnJam.setCellValueFactory(new PropertyValueFactory<>("jam"));
        columnLokasi.setCellValueFactory(new PropertyValueFactory<>("lokasi"));
        columnNomorTiket.setCellValueFactory(new PropertyValueFactory<>("nomorTiket"));

        tableView.getColumns().addAll(columnNama, columnJumlahTiket, columnJam, columnLokasi, columnNomorTiket);

        // Baca data dari file dan tampilkan dalam tabel
        readDataFromFile(tableView);

        // Tombol Update dan Delete
        Button buttonUpdate = new Button("Update");
        Button buttonDelete = new Button("Delete");

        buttonUpdate.setOnAction(e -> {updateOrder(addNama.getText(), addJumlah.getText(), addJam.getText(),addLokasi.getText(),addNomor.getText());
            addNama.clear();
            addJumlah.clear();
            addJam.clear();
            addLokasi.clear();
            addNomor.clear();
        });
        buttonDelete.setOnAction(e -> deleteOrder(tableView));

        HBox buttonBox = new HBox(10, buttonUpdate, buttonDelete);

        vbox.getChildren().addAll(text, labelNama, addNama, labelJumlahTiket, addJumlah,
                labelJam, addJam, labelLokasi, addLokasi, labelNomorTiket, addNomor,
                buttonPesan, tableView, buttonBox);

        Scene scene = new Scene(vbox, 800, 400);


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void refreshTable() {
        // Baca ulang data dari file dan perbarui tabel
        TableView<Order> tableView = findTableView();
        if (tableView != null) {
            tableView.getItems().clear();
            readDataFromFile(tableView);
        }
    }

    private TableView<Order> findTableView() {
        Scene scene = Stage.getWindows().get(0).getScene();
        for (var node : scene.getRoot().getChildrenUnmodifiable()) {
            if (node instanceof TableView) {
                return (TableView<Order>) node;
            }
        }
        return null;
    }

    /**
     * Method untuk mengupdate pesanan dalam file tiket_data.txt.
     * @param nama Nama baru untuk pesanan.
     * @param jumlahTiketStr Jumlah tiket baru untuk pesanan.
     * @param jam Jam baru untuk pesanan.
     * @param lokasi lokasi baru untuk pesanan.
     * @param nomorTiket Nomor tiket baru untuk pesanan.
     */
    public void updateOrder(String nama, String jumlahTiketStr, String jam, String lokasi, String nomorTiket){
        Order selectedOrder = tableView.getSelectionModel().getSelectedItem();
        if(selectedOrder != null){
            try {
                File inputFile = new File("tiket_data.txt");
                File tempFile = new File("temp.txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                String line;

                validateInput(nama, jumlahTiketStr, jam, lokasi, nomorTiket);

                while((line = reader.readLine()) != null){
                    String[] parts = line.split(",");
                    String name = parts[0].trim();
                    int jumlahTiket = Integer.parseInt(parts[1].trim());
                    String time = parts[2].trim();
                    String location = parts[3].trim();
                    String ticketNumber = parts[4].trim();

                    Order currentOrder = new Order(name, jumlahTiket, time, location, ticketNumber);

                    if (currentOrder.equals(selectedOrder)) {
                        writer.write(nama +","+jumlahTiketStr+","+jam+","+lokasi+","+nomorTiket);
                        writer.newLine();
                    }else{
                        writer.write(name +","+jumlahTiket+","+time+","+location+","+ticketNumber);
                        writer.newLine();
                    }

                }

                writer.close();
                reader.close();
                inputFile.delete();

                // Ganti file asli dengan file sementara
                if (tempFile.renameTo(inputFile)) {
                    Pemberitahuan("Pesanan diperbarui.");
                } else {
                    Peringatan("Error saat memperbarui pesanan.");
                }

                refreshTable();
            }catch (IOException e){
                Peringatan("Error saat menghapus pesanan dari file.");
            }
        }else{
            Peringatan("Pilih Data yang diperbarui");
        }
    }

    /**
     * Method untuk memeriksa pesanan dari data tabel yang sudah di tekan.
     * Kemudian pindah ke method lain yang digunakan untuk menghilangkan data dari file.
     * @param tableView Tabel yang berisi pesanan.
     */
    private void deleteOrder(TableView<Order> tableView) {
        Order selectedOrder = tableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            // Hapus data dari file
            deleteOrderFromFile(selectedOrder);
            // Hapus dari tabel
            tableView.getItems().remove(selectedOrder);
        } else {
            Peringatan("Pilih pesanan untuk dihapus.");
        }
    }

    /**
     * Method untuk menghapus pesanan dalam file tiket_data.txt.
     * @param orderToDelete Untuk menghapus pesanan dalam file tiket_data.txt.
     */
    private void deleteOrderFromFile(Order orderToDelete) {
        try {
            File inputFile = new File("tiket_data.txt");
            File tempFile = new File("temp.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String nama = parts[0].trim();
                    int jumlahTiket = Integer.parseInt(parts[1].trim());
                    String jam = parts[2].trim();
                    String lokasi = parts[3].trim();
                    String nomorTiket = parts[4].trim();

                    Order currentOrder = new Order(nama, jumlahTiket, jam, lokasi, nomorTiket);


                    if (!currentOrder.equals(orderToDelete)) {
                        writer.write(line);
                        writer.newLine();
                    }

                }
            }

            writer.close();
            reader.close();
            inputFile.delete();

            // Ganti file asli dengan file sementara
            if (tempFile.renameTo(inputFile)) {
                Pemberitahuan("Pesanan dihapus.");
            } else {
                Peringatan("Error saat menghapus pesanan.");
            }

        } catch (IOException e) {
            Peringatan("Error saat menghapus pesanan dari file.");
        }
    }

    /**
     * Method untuk validasi input yang dimasukkan.
     * @param nama Nama pemesan.
     * @param jumlahTiketStr Jumlah tiket yang dipesan.
     * @param jam Jam berapa tiket yang mau dipesan.
     * @param lokasi Lokasi tempat pemesan.
     * @param nomorTiket Nomor tiket yang dipesan.
     * @return Validasi input bernilai true atau false.
     * Method ini digunakan sebagai tolak ukur pengisian data.
     */
    private boolean validateInput(String nama, String jumlahTiketStr, String jam, String lokasi, String nomorTiket) {

        if (nama.isEmpty() || jumlahTiketStr.isEmpty() || jam.isEmpty() || lokasi.isEmpty() || nomorTiket.isEmpty()) {
            Peringatan("Kolom tidak boleh kosong!");
            return false;
        }

        try{
            Long.parseLong(jumlahTiketStr);
            Long.parseLong(nomorTiket);
        }catch(NumberFormatException e){
            Peringatan("Kolom Jumlah Tiket / Nomor tiker harus angka!!.");
            return false;
        }

        return true;
    }

    /**
     * Method untuk menyimpan data pemesan ke dalam file tiket_data.txt.
     * Method ini digunakan saat menggunakan tombol 'Pesan Tiket'.
     * @param nama Nama pemesan.
     * @param jumlahTiket Jumlah tiket yang dipesan.
     * @param jam Jam berapa tiket yang mau dipesan.
     * @param lokasi Lokasi tempat pemesan.
     * @param nomorTiket Nomor tiket yang dipesan.

     */
    private void saveOrderData(String nama, int jumlahTiket, String jam, String lokasi, String nomorTiket) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE_PATH, true))) {
            writer.write(nama + "," + jumlahTiket + "," + jam + "," + lokasi + "," + nomorTiket);
            writer.newLine();
        } catch (IOException e) {
            Peringatan("Error saat menyimpan data ke file.");
        }
    }

    /**
     * Method untuk membaca data dari file kemudian ditampilkan di aplikasi.
     * @param tableView Tabel yang berisi pesanan.
     */
    private void readDataFromFile(TableView<Order> tableView) {
        ObservableList<Order> data = FXCollections.observableArrayList();

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String nama = parts[0].trim();
                    int jumlahTiket = Integer.parseInt(parts[1].trim());
                    String jam = parts[2].trim();
                    String lokasi = parts[3].trim();
                    String nomorTiket = parts[4].trim();

                    data.add(new Order(nama, jumlahTiket, jam, lokasi, nomorTiket));
                }
            }
        } catch (IOException e) {
           Peringatan("Terjadi Kesalahan saat membaca File!");
        }

        tableView.setItems(data);
    }

    /**
     * Method untuk memberikan notifikasi peringatan pada saat ada sesuatu yang salah.
     * @param message adalah string yang akan ditampilkan sebagai peringatan tersebut.
     */
    private void Peringatan(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    /**
     * Method untuk memberikan notifikasi pada saat ada yang perlu di informasikan.
     * @param message adalah string yang akan ditampilkan sebagai informasi.
     */
    private void Pemberitahuan(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pemberitahuan");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Kelas yang merepresentasikan pesanan tiket.
     */
    public static class Order {
        private final String nama;
        private final int jumlahTiket;
        private final String jam;
        private final String lokasi;
        private final String nomorTiket;

        /**
         * Konstruktor untuk membuat objek Order.
         * @param nama Nama pada pesanan.
         * @param jumlahTiket Jumlah tiket pada pesanan.
         * @param jam Jam pada pesanan.
         * @param lokasi Lokasi pada pesanan.
         * @param nomorTiket Nomor tiket pada pesanan.
         */
        public Order(String nama, int jumlahTiket, String jam, String lokasi, String nomorTiket) {
            this.nama = nama;
            this.jumlahTiket = jumlahTiket;
            this.jam = jam;
            this.lokasi = lokasi;
            this.nomorTiket = nomorTiket;
        }

        public String getNama() {
            return nama;
        }

        public int getJumlahTiket() {
            return jumlahTiket;
        }

        public String getJam() {
            return jam;
        }

        public String getLokasi() {
            return lokasi;
        }

        public String getNomorTiket() {
            return nomorTiket;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Order order = (Order) obj;
            return Objects.equals(nama, order.nama);
        }

    }
}
