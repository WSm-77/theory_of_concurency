package app;

public class Main {
    public static void main(String[] args) {
        SyncList syncList = new SyncList();

        for (Integer i = 0; i < 10; i++) {
            syncList.add(i);
        }

        System.out.println(syncList.contains(2));
    }
}
