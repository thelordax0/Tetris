package tetris;

import java.io.*;
import java.util.Base64;

public class MusicLoader {
    public static void createMusicFile() {
        // Tetris müziği Base64 formatında (kısaltılmış örnek)
        String musicBase64 = "UklGRjIAAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YRAAAAAA...";  // Tam Base64 string buraya gelecek
        
        try {
            byte[] musicBytes = Base64.getDecoder().decode(musicBase64);
            
            // Proje klasöründe müzik dosyasını oluştur
            FileOutputStream fos = new FileOutputStream("tetris_theme.wav");
            fos.write(musicBytes);
            fos.close();
            
            System.out.println("Müzik dosyası başarıyla oluşturuldu.");
        } catch (Exception e) {
            System.err.println("Müzik dosyası oluşturulamadı: " + e.getMessage());
        }
    }
} 