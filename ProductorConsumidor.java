package ProductorConsumidor;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

// Clase que representa la cola compartida
class Cola {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacidad;
    
    public Cola(int capacidad) {
        this.capacidad = capacidad;
    }

    public synchronized void add(int valor) throws InterruptedException {
        while (queue.size() == capacidad) {
            wait();
        }
        queue.add(valor);
        notifyAll();
    }

    public synchronized int remove() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        int valor = queue.poll();
        notifyAll();
        return valor;
    }
}

// Hilo Productor
class Productor extends Thread {
    private final Cola cola;
    private final Random random = new Random();

    public Productor(Cola cola) {
        this.cola = cola;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 1000; i++) {
                int numero = random.nextInt(100) + 1;
                cola.add(numero);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Hilo Consumidor
class Consumidor extends Thread {
    private final Cola cola;
    private final int[] conteoDecenas = new int[10];

    public Consumidor(Cola cola) {
        this.cola = cola;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int numero = cola.remove();
                if (numero == -1) break;
                int indice = (numero - 1) / 10;
                if (indice >= 0 && indice < conteoDecenas.length) {
                    conteoDecenas[indice]++;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int[] getConteoDecenas() {
        return conteoDecenas;
    }
}

// Clase principal para ejecutar el programa
public class ProductorConsumidor {
    public static void main(String[] args) throws InterruptedException {
        final int CAPACIDAD = 100;
        Cola cola = new Cola(CAPACIDAD);
        Productor productor = new Productor(cola);
        Consumidor consumidor = new Consumidor(cola);

        productor.start();
        consumidor.start();

        
    }
}
