import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    public static void main(String[] args) {
        final String saveDir = "c://Games//savegames";

        GameProgress player1 = new GameProgress(100, 20, 10, 1000);
        GameProgress player2 = new GameProgress(80, 70, 30, 5200);
        GameProgress player3 = new GameProgress(13, 28, 28, 6800);

        System.out.println("Дано:");
        System.out.println("player1: " + player1);
        System.out.println("player2: " + player2);
        System.out.println("player3: " + player3);

        saveGame(saveDir + "//player1.dat", player1);
        saveGame(saveDir + "//player2.dat", player2);
        saveGame(saveDir + "//player3.dat", player3);

        if (zipFiles(saveDir + "//allPlayersProgress.zip",
                saveDir + "//player1.dat",
                saveDir + "//player2.dat",
                saveDir + "//player3.dat")) {
            cleanupSaveDir(saveDir);
        } else {
            return;
        }

        openZip(saveDir + "//allPlayersProgress.zip", saveDir);
        System.out.println("player2: " + openProgress(saveDir + "//player2.dat"));
    }

    private static GameProgress openProgress(String file) {
        GameProgress gameProgress = null;
        try (FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis)) {
            gameProgress = (GameProgress) ois.readObject();
            System.out.println("Восстановлено сохранение из файла " + file);
        } catch (Exception e) {
            System.out.println("Ошибка! Не удалось восстановить сохранение! " + e);
        }
        return gameProgress;
    }

    private static void openZip(String arcPath, String destPath) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(arcPath))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                FileOutputStream fout = new FileOutputStream(name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
                System.out.println("Файл " + name + " извлечён из архива");
            }
        } catch (Exception e) {
            System.out.println("Ошибка! Не удалось извлечь сохранение из архива! " + e);
        }
    }

    private static void cleanupSaveDir(String saveDir) {
        File dir = new File(saveDir);
        for (File item : dir.listFiles(pathname -> pathname.toString().toLowerCase().endsWith(".dat"))) {
            if (item.delete()) System.out.println(item + " удалён");
        }
    }

    private static boolean zipFiles(String arcPath, String... files) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(arcPath))) {
            for (String file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry entry = new ZipEntry(file);
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                    System.out.println(file + " добавлен в архив " + arcPath);
                } catch (Exception e) {
                    System.out.println("Ошибка! Не удалось добавить сохранение в архив! " + e);
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка! Не удалось добавить сохранение в архив! " + e);
            return false;
        }
        return true;
    }

    private static void saveGame(String filePath, GameProgress gameProgress) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(gameProgress);
            System.out.println("Создан файл " + filePath);
        } catch (Exception e) {
            System.out.println("Ошибка! Не удалось выполнить сохранение! " + e);
        }
    }
}