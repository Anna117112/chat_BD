package geekbrains.Java3_lesson_4;

public class PrintABC {
    private Object mon = new Object();
    private String cuncurrent = "A";
    public static void main(String[] args) {
        PrintABC printABC = new PrintABC();
        Thread thread1 = new Thread(()->{
            printABC.printA();

        });
        Thread thread2 = new Thread(()->{
            printABC.printB();
        });
        Thread thread3 = new Thread(()->{
            printABC.printC();
        });
        thread1.start();
        thread2.start();
        thread3.start();
    }

    private void printA() {
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (cuncurrent != "A") {
                        mon.wait();
                    }
                    System.out.print("A");
                    cuncurrent = "B";
                    mon.notifyAll();
                }


            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    private void printB() {
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (cuncurrent != "B") {
                        mon.wait();
                    }
                    System.out.print("B");
                    cuncurrent = "C";
                    mon.notifyAll();
                }


            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    private void printC() {
        synchronized (mon) {
            try {
                for (int i = 0; i < 5; i++) {
                    while (cuncurrent != "C") {
                        mon.wait();
                    }
                    System.out.print("C");
                    cuncurrent = "A";
                    mon.notifyAll();
                }


            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

}

